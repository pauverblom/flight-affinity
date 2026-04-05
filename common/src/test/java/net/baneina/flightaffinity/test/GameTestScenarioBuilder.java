package net.baneina.flightaffinity.test;

import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;

import java.lang.reflect.Field;
import java.util.Set;

public class GameTestScenarioBuilder {
    private final GameTestHelper context;
    private boolean hasFlightAffinity;
    private boolean hasAquaAffinity;
    private boolean isAirborne;
    private boolean inWater;
    private boolean inLava;
    private String vehicleType = "none";
    private float expectedMin;
    private float expectedMax;
    private String scenarioName;

    public GameTestScenarioBuilder(GameTestHelper context) {
        this.context = context;
    }

    public GameTestScenarioBuilder withFlightAffinity() {
        this.hasFlightAffinity = true;
        return this;
    }

    public GameTestScenarioBuilder withAquaAffinity() {
        this.hasAquaAffinity = true;
        return this;
    }

    public GameTestScenarioBuilder setAirborne(boolean airborne) {
        this.isAirborne = airborne;
        return this;
    }

    public GameTestScenarioBuilder inWater() {
        this.inWater = true;
        return this;
    }

    public GameTestScenarioBuilder inLava() {
        this.inLava = true;
        return this;
    }

    public GameTestScenarioBuilder withVehicle(String vehicleType) {
        this.vehicleType = vehicleType;
        return this;
    }

    public GameTestScenarioBuilder expectedSpeed(float min, float max) {
        this.expectedMin = min;
        this.expectedMax = max;
        return this;
    }

    public GameTestScenarioBuilder name(String name) {
        this.scenarioName = name;
        return this;
    }

    public void run() {
        /*
         * In MC 1.20.4, makeMockPlayer() creates a Player whose bounding box
         * becomes null after it is added to the world, causing NPEs when the
         * server tries to check fluid submersion.
         *
         * We work around this by NOT adding the player to the world for
         * non-vehicle tests.  Instead we:
         *   • set the player's position (initialises the AABB),
         *   • configure enchantments and ground state,
         *   • for water tests, inject the fluid-on-eyes flag via reflection,
         *   • call getDestroySpeed() directly.
         *
         * This still exercises the Flight Affinity mixin inside
         * Player.getDestroySpeed because the mixin is loaded via the
         * Mixin subsystem — it modifies the Player class itself, not
         * just ServerPlayer.
         */
        Player mockPlayer = context.makeMockPlayer();

        // Initialise position so the AABB is non-null.
        BlockPos spawnPos = context.absolutePos(new BlockPos(0, 0, 0));
        mockPlayer.setPos(spawnPos.getX() + 0.5, spawnPos.getY() + 0.2, spawnPos.getZ() + 0.5);

        // Equip helmet with enchantments.
        if (hasFlightAffinity || hasAquaAffinity) {
            ItemStack helmet = new ItemStack(FlightAffinityTestConstants.HELMET_TYPE);
            if (hasFlightAffinity) {
                helmet.enchant(ModEnchantments.FLIGHT_AFFINITY, 1);
            }
            if (hasAquaAffinity) {
                helmet.enchant(Enchantments.AQUA_AFFINITY, 1);
            }
            mockPlayer.setItemSlot(EquipmentSlot.HEAD, helmet);
        }

        // For water tests, inject the fluid-on-eyes state via reflection so
        // Player.isEyeInFluid(FluidTags.WATER) returns true without placing
        // actual water blocks (which triggers the AABB null bug).
        if (inWater) {
            injectFluidOnEyes(mockPlayer);
        }
        // Lava has no mining-speed penalty in vanilla, so we don't need
        // to place actual lava blocks or set any fluid state.

        // Vehicle tests still need the world.
        if (!"none".equals(vehicleType)) {
            context.getLevel().addFreshEntity(mockPlayer);
        }

        if ("minecart".equals(vehicleType)) {
            var minecart = net.minecraft.world.entity.EntityType.MINECART.create(context.getLevel());
            if (minecart == null) { context.fail(scenarioName + " failed to create minecart"); return; }
            minecart.setPos(mockPlayer.getX(), mockPlayer.getY(), mockPlayer.getZ());
            context.getLevel().addFreshEntity(minecart);
            if (!mockPlayer.startRiding(minecart)) { context.fail(scenarioName + " failed to mount minecart"); return; }
        } else if ("horse".equals(vehicleType)) {
            var horse = net.minecraft.world.entity.EntityType.HORSE.create(context.getLevel());
            if (horse == null) { context.fail(scenarioName + " failed to create horse"); return; }
            horse.setPos(mockPlayer.getX(), mockPlayer.getY(), mockPlayer.getZ());
            context.getLevel().addFreshEntity(horse);
            if (!mockPlayer.startRiding(horse)) { context.fail(scenarioName + " failed to mount horse"); return; }
        }

        // Place the target block that we measure speed against.
        BlockPos targetPos = new BlockPos(0, 4, 0);
        context.setBlock(targetPos, FlightAffinityTestConstants.DEFAULT_TARGET_BLOCK);

        if ("none".equals(vehicleType)) {
            mockPlayer.setOnGround(!isAirborne);
        } else {
            mockPlayer.setOnGround(false);
        }

        // For non-vehicle tests we can measure immediately — the player
        // is fully configured and getDestroySpeed() is a pure calculation.
        // For vehicle tests a tick delay is still needed so the passenger
        // state settles.
        if ("none".equals(vehicleType)) {
            measureAndAssert(mockPlayer, targetPos);
        } else {
            context.runAfterDelay(FlightAffinityTestConstants.TICK_DELAY, () -> {
                measureAndAssert(mockPlayer, targetPos);
            });
        }
    }

    private void measureAndAssert(Player mockPlayer, BlockPos targetPos) {
        float breakingSpeed = mockPlayer.getDestroySpeed(
                context.getLevel().getBlockState(context.absolutePos(targetPos)));

        if (breakingSpeed < expectedMin || expectedMax < breakingSpeed) {
            context.fail(
                    scenarioName + " expected speed in [" + expectedMin + ", " + expectedMax
                            + "] but got " + breakingSpeed
                            + " (onGround=" + mockPlayer.onGround()
                            + ", passenger=" + mockPlayer.isPassenger() + ")"
            );
            return;
        }
        context.succeed();
    }

    /**
     * Sets the private {@code Entity.fluidOnEyes} field so that
     * {@code isEyeInFluid(FluidTags.WATER)} returns {@code true}.
     *
     * <p>On NeoForge, {@code isEyeInFluid(FluidTags.WATER)} is patched to delegate
     * to {@code isEyeInFluidType(WATER_TYPE)}, which checks the NeoForge-specific
     * {@code forgeFluidTypeOnEyes} field instead.  We therefore also set that field
     * via reflection when it exists.
     */
    @SuppressWarnings("unchecked")
    private void injectFluidOnEyes(Player player) {
        // 1. Vanilla path: add FluidTags.WATER to the fluidOnEyes set.
        try {
            Field field = Entity.class.getDeclaredField("fluidOnEyes");
            field.setAccessible(true);
            Set<Object> eyes = (Set<Object>) field.get(player);
            eyes.add(FluidTags.WATER);
        } catch (ReflectiveOperationException e) {
            context.fail(scenarioName + " could not inject fluidOnEyes: " + e.getMessage());
            return;
        }

        // 2. NeoForge path: set the forgeFluidTypeOnEyes field to WATER_TYPE.
        //    This field only exists on NeoForge; on Fabric the NoSuchFieldException
        //    is silently ignored because the vanilla path above is sufficient.
        try {
            Field forgeField = Entity.class.getDeclaredField("forgeFluidTypeOnEyes");
            forgeField.setAccessible(true);
            // Resolve NeoForgeMod.WATER_TYPE via reflection to avoid a compile-time
            // dependency on NeoForge classes from the common module.
            Class<?> neoForgeMod = Class.forName("net.neoforged.neoforge.common.NeoForgeMod");
            Field waterTypeHolder = neoForgeMod.getDeclaredField("WATER_TYPE");
            waterTypeHolder.setAccessible(true);
            // WATER_TYPE is a Holder<FluidType> (DeferredHolder); call .value() to get the FluidType.
            Object holder = waterTypeHolder.get(null);
            Object waterType = holder.getClass().getMethod("value").invoke(holder);
            forgeField.set(player, waterType);
        } catch (ClassNotFoundException | NoSuchFieldException ignored) {
            // Not running on NeoForge — vanilla fluidOnEyes is sufficient.
        } catch (ReflectiveOperationException e) {
            context.fail(scenarioName + " could not inject forgeFluidTypeOnEyes: " + e.getMessage());
        }
    }
}
