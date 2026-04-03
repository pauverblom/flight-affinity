package net.baneina.flightaffinity.test;

import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;

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
        Player mockPlayer = context.makeMockPlayer(GameType.SURVIVAL);
        var registry = context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        if (hasFlightAffinity || hasAquaAffinity) {
            ItemStack helmet = new ItemStack(FlightAffinityTestConstants.HELMET_TYPE);
            if (hasFlightAffinity) {
                helmet.enchant(registry.getOrThrow(ModEnchantments.FLIGHT_AFFINITY), 1);
            }
            if (hasAquaAffinity) {
                helmet.enchant(registry.getOrThrow(Enchantments.AQUA_AFFINITY), 1);
            }
            mockPlayer.setItemSlot(EquipmentSlot.HEAD, helmet);
        }

        if ("none".equals(vehicleType)) {
            mockPlayer.setOnGround(!isAirborne);
        } else {
            mockPlayer.setOnGround(false);
        }

        if (inWater) {
            context.setBlock(new BlockPos(0, 0, 0), Blocks.WATER);
            context.setBlock(new BlockPos(0, 1, 0), Blocks.WATER);
            context.setBlock(new BlockPos(0, 2, 0), Blocks.WATER);
        } else if (inLava) {
            context.setBlock(new BlockPos(0, 0, 0), Blocks.LAVA);
            context.setBlock(new BlockPos(0, 1, 0), Blocks.LAVA);
            context.setBlock(new BlockPos(0, 2, 0), Blocks.LAVA);
        }

        BlockPos spawnPos = context.absolutePos(new BlockPos(0, 0, 0));
        mockPlayer.teleportTo(spawnPos.getX() + 0.5, spawnPos.getY() + 0.2, spawnPos.getZ() + 0.5);

        if ("minecart".equals(vehicleType)) {
            var minecart = net.minecraft.world.entity.EntityType.MINECART.create(context.getLevel(), MobSpawnType.COMMAND);
            if (minecart == null) {
                context.fail(scenarioName + " failed to create minecart");
                return;
            }
            minecart.setPos(mockPlayer.getX(), mockPlayer.getY(), mockPlayer.getZ());
            context.getLevel().addFreshEntity(minecart);
            if (!mockPlayer.startRiding(minecart)) {
                context.fail(scenarioName + " failed to mount minecart");
                return;
            }
        } else if ("horse".equals(vehicleType)) {
            var horse = net.minecraft.world.entity.EntityType.HORSE.create(context.getLevel(), MobSpawnType.COMMAND);
            if (horse == null) {
                context.fail(scenarioName + " failed to create horse");
                return;
            }
            horse.setPos(mockPlayer.getX(), mockPlayer.getY(), mockPlayer.getZ());
            context.getLevel().addFreshEntity(horse);
            if (!mockPlayer.startRiding(horse)) {
                context.fail(scenarioName + " failed to mount horse");
                return;
            }
        }

        context.getLevel().addFreshEntity(mockPlayer);

        BlockPos targetPos = new BlockPos(0, 4, 0);
        context.setBlock(targetPos, FlightAffinityTestConstants.DEFAULT_TARGET_BLOCK);

        context.runAfterDelay(FlightAffinityTestConstants.TICK_DELAY, () -> {
            if ("none".equals(vehicleType) && !isAirborne) {
                mockPlayer.setOnGround(true);
            }

            float breakingSpeed = mockPlayer.getDestroySpeed(context.getLevel().getBlockState(context.absolutePos(targetPos)));

            if (breakingSpeed < expectedMin || expectedMax < breakingSpeed) {
                context.fail(
                        scenarioName + " expected speed in [" + expectedMin + ", " + expectedMax + "] but got " + breakingSpeed
                                + " (onGround=" + mockPlayer.onGround() + ", passenger=" + mockPlayer.isPassenger() + ")"
                );
                return;
            }
            context.succeed();
        });
    }
}

