package net.baneina.flightaffinity.test;

import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.baneina.flightaffinity.rules.MiningSpeedRules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.Test;

import static net.baneina.flightaffinity.rules.MiningSpeedRules.Environment.*;
import static net.baneina.flightaffinity.rules.MiningSpeedRules.Stance.AIRBORNE;
import static net.baneina.flightaffinity.rules.MiningSpeedRules.Stance.GROUNDED;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommonGameTests {

    private static final float SPEED_100_MIN = 0.99f;
    private static final float SPEED_100_MAX = 1.01f;
    private static final float SPEED_20_MIN = 0.19f;
    private static final float SPEED_20_MAX = 0.21f;
    private static final float SPEED_4_MIN = 0.039f;
    private static final float SPEED_4_MAX = 0.041f;

    private static void runFlightAffinityScenario(
            GameTestHelper context,
            boolean hasFlightAffinity,
            boolean useAquaAffinity,
            boolean onGround,
            boolean inWater,
            boolean inLava,
            float expectedMin,
            float expectedMax,
            String scenarioName
    ) {
        Player mockPlayer = context.makeMockPlayer(GameType.SURVIVAL);
        var registry = context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        if (hasFlightAffinity || useAquaAffinity) {
            ItemStack helmet = new ItemStack(Items.DIAMOND_HELMET);
            if (hasFlightAffinity) {
                var flightAffinity = registry.getOrThrow(ModEnchantments.FLIGHT_AFFINITY);
                helmet.enchant(flightAffinity, 1);
            }
            if (useAquaAffinity) {
                helmet.enchant(registry.getOrThrow(Enchantments.AQUA_AFFINITY), 1);
            }
            mockPlayer.setItemSlot(EquipmentSlot.HEAD, helmet);
        }

        mockPlayer.setOnGround(onGround);

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

        context.getLevel().addFreshEntity(mockPlayer);

        BlockPos targetPos = new BlockPos(0, 4, 0);
        context.setBlock(targetPos, Blocks.STONE);

        // Fluid state on entities can be finalized on the next tick in GameTests.
        context.runAfterDelay(2, () -> {
            float breakingSpeed = mockPlayer.getDestroySpeed(context.getLevel().getBlockState(context.absolutePos(targetPos)));

            if (breakingSpeed < expectedMin || breakingSpeed > expectedMax) {
                context.fail(Component.literal(scenarioName + " expected speed in [" + expectedMin + ", " + expectedMax + "] but got " + breakingSpeed));
                return;
            }
            context.succeed();
        });
    }

    private static void runFlightAffinityScenario(
            GameTestHelper context,
            boolean useAquaAffinity,
            boolean onGround,
            boolean inWater,
            boolean inLava,
            float expectedMin,
            float expectedMax,
            String scenarioName
    ) {
        runFlightAffinityScenario(context, true, useAquaAffinity, onGround, inWater, inLava, expectedMin, expectedMax, scenarioName);
    }

    public static void testFlightAffinityAirGroundedFlightOnly(GameTestHelper context) {
        runFlightAffinityScenario(context, false, true, false, false, SPEED_100_MIN, SPEED_100_MAX, "air_grounded_flight_only");
    }

    public static void testFlightAffinityAirGroundedBoth(GameTestHelper context) {
        runFlightAffinityScenario(context, true, true, false, false, SPEED_100_MIN, SPEED_100_MAX, "air_grounded_both");
    }

    public static void testFlightAffinityAirAirborneFlightOnly(GameTestHelper context) {
        runFlightAffinityScenario(context, false, false, false, false, SPEED_100_MIN, SPEED_100_MAX, "air_airborne_flight_only");
    }

    public static void testFlightAffinityAirAirborneBoth(GameTestHelper context) {
        runFlightAffinityScenario(context, true, false, false, false, SPEED_100_MIN, SPEED_100_MAX, "air_airborne_both");
    }

    public static void testFlightAffinityWaterGroundedFlightOnly(GameTestHelper context) {
        runFlightAffinityScenario(context, false, true, true, false, SPEED_20_MIN, SPEED_20_MAX, "water_grounded_flight_only");
    }

    public static void testFlightAffinityWaterGroundedBoth(GameTestHelper context) {
        runFlightAffinityScenario(context, true, true, true, false, SPEED_100_MIN, SPEED_100_MAX, "water_grounded_both");
    }

    public static void testFlightAffinityWaterFloatingFlightOnly(GameTestHelper context) {
        runFlightAffinityScenario(context, false, false, true, false, SPEED_20_MIN, SPEED_20_MAX, "water_floating_flight_only");
    }

    public static void testFlightAffinityWaterFloatingBoth(GameTestHelper context) {
        runFlightAffinityScenario(context, true, false, true, false, SPEED_100_MIN, SPEED_100_MAX, "water_floating_both");
    }

    public static void testFlightAffinityLavaGroundedFlightOnly(GameTestHelper context) {
        runFlightAffinityScenario(context, false, true, false, true, SPEED_100_MIN, SPEED_100_MAX, "lava_grounded_flight_only");
    }

    public static void testFlightAffinityLavaGroundedBoth(GameTestHelper context) {
        runFlightAffinityScenario(context, true, true, false, true, SPEED_100_MIN, SPEED_100_MAX, "lava_grounded_both");
    }

    public static void testFlightAffinityLavaFloatingFlightOnly(GameTestHelper context) {
        runFlightAffinityScenario(context, false, false, false, true, SPEED_100_MIN, SPEED_100_MAX, "lava_floating_flight_only");
    }

    public static void testFlightAffinityLavaFloatingBoth(GameTestHelper context) {
        runFlightAffinityScenario(context, true, false, false, true, SPEED_100_MIN, SPEED_100_MAX, "lava_floating_both");
    }

    public static void testFlightAffinityAirAirborneNoAffinity(GameTestHelper context) {
        runFlightAffinityScenario(context, false, false, false, false, false, SPEED_20_MIN, SPEED_20_MAX, "air_airborne_no_affinity");
    }

    public static void testFlightAffinityWaterFloatingNoAffinity(GameTestHelper context) {
        runFlightAffinityScenario(context, false, false, false, true, false, SPEED_4_MIN, SPEED_4_MAX, "water_floating_no_affinity");
    }

    public static void testFlightAffinityAirAirborneUnequipAffinity(GameTestHelper context) {
        Player mockPlayer = context.makeMockPlayer(GameType.SURVIVAL);
        var registry = context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        ItemStack helmet = new ItemStack(Items.DIAMOND_HELMET);
        helmet.enchant(registry.getOrThrow(ModEnchantments.FLIGHT_AFFINITY), 1);
        mockPlayer.setItemSlot(EquipmentSlot.HEAD, helmet);
        mockPlayer.setOnGround(false);

        BlockPos spawnPos = context.absolutePos(new BlockPos(0, 0, 0));
        mockPlayer.teleportTo(spawnPos.getX() + 0.5, spawnPos.getY() + 0.2, spawnPos.getZ() + 0.5);
        context.getLevel().addFreshEntity(mockPlayer);

        BlockPos targetPos = new BlockPos(0, 4, 0);
        context.setBlock(targetPos, Blocks.STONE);

        context.runAfterDelay(2, () -> {
            float before = mockPlayer.getDestroySpeed(context.getLevel().getBlockState(context.absolutePos(targetPos)));
            if (before < SPEED_100_MIN || before > SPEED_100_MAX) {
                context.fail(Component.literal("air_airborne_unequip_affinity expected initial speed in [" + SPEED_100_MIN + ", " + SPEED_100_MAX + "] but got " + before));
                return;
            }

            mockPlayer.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);

            context.runAfterDelay(2, () -> {
                mockPlayer.setOnGround(false);
                float after = mockPlayer.getDestroySpeed(context.getLevel().getBlockState(context.absolutePos(targetPos)));
                if (after < SPEED_20_MIN || after > SPEED_20_MAX) {
                    context.fail(Component.literal("air_airborne_unequip_affinity expected post-unequip speed in [" + SPEED_20_MIN + ", " + SPEED_20_MAX + "] but got " + after));
                    return;
                }
                context.succeed();
            });
        });
    }


    private static void runAdvancedScenario(
            GameTestHelper context,
            boolean hasFlightAffinity,
            boolean isAirborne,
            String vehicleType,
            int fatigueLevel,
            boolean usePickaxe,
            net.minecraft.world.level.block.Block targetBlock,
            float expectedMin,
            float expectedMax,
            String scenarioName
    ) {
        Player mockPlayer = context.makeMockPlayer(GameType.SURVIVAL);
        var registry = context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        if (hasFlightAffinity) {
            ItemStack helmet = new ItemStack(Items.DIAMOND_HELMET);
            var flightAffinity = registry.getOrThrow(ModEnchantments.FLIGHT_AFFINITY);
            helmet.enchant(flightAffinity, 1);
            mockPlayer.setItemSlot(EquipmentSlot.HEAD, helmet);
        }

        if (usePickaxe) {
            mockPlayer.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_PICKAXE));
        }

        if ("none".equals(vehicleType)) {
            mockPlayer.setOnGround(!isAirborne);
        } else {
            // Vehicle scenarios in this suite are intended to exercise airborne penalty behavior.
            mockPlayer.setOnGround(false);
        }

        BlockPos spawnPos = context.absolutePos(new BlockPos(0, 0, 0));
        mockPlayer.teleportTo(spawnPos.getX() + 0.5, spawnPos.getY() + 0.2, spawnPos.getZ() + 0.5);

        if ("minecart".equals(vehicleType)) {
            var minecart = net.minecraft.world.entity.EntityType.MINECART.create(context.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
            if (minecart == null) {
                context.fail(Component.literal(scenarioName + " failed to create minecart"));
                return;
            }
            minecart.setPos(mockPlayer.getX(), mockPlayer.getY(), mockPlayer.getZ());
            context.getLevel().addFreshEntity(minecart);
            if (!mockPlayer.startRiding(minecart)) {
                context.fail(Component.literal(scenarioName + " failed to mount minecart"));
                return;
            }
        } else if ("horse".equals(vehicleType)) {
            var horse = net.minecraft.world.entity.EntityType.HORSE.create(context.getLevel(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
            if (horse == null) {
                context.fail(Component.literal(scenarioName + " failed to create horse"));
                return;
            }
            horse.setPos(mockPlayer.getX(), mockPlayer.getY(), mockPlayer.getZ());
            context.getLevel().addFreshEntity(horse);
            if (!mockPlayer.startRiding(horse)) {
                context.fail(Component.literal(scenarioName + " failed to mount horse"));
                return;
            }
        }

        if (fatigueLevel > 0) {
            var fatigue = net.minecraft.world.effect.MobEffects.MINING_FATIGUE;
            mockPlayer.addEffect(new net.minecraft.world.effect.MobEffectInstance(fatigue, 1000, fatigueLevel - 1, false, false));
        }

        context.getLevel().addFreshEntity(mockPlayer);

        BlockPos targetPos = new BlockPos(0, 4, 0);
        context.setBlock(targetPos, targetBlock);

        context.runAfterDelay(2, () -> {
            // In GameTests the physics step can leave freshly spawned entities slightly airborne.
            if ("none".equals(vehicleType) && !isAirborne) {
                mockPlayer.setOnGround(true);
            }

            float breakingSpeed = mockPlayer.getDestroySpeed(context.getLevel().getBlockState(context.absolutePos(targetPos)));

            if (breakingSpeed < expectedMin || breakingSpeed > expectedMax) {
                context.fail(Component.literal(
                        scenarioName + " expected speed in [" + expectedMin + ", " + expectedMax + "] but got " + breakingSpeed
                                + " (onGround=" + mockPlayer.onGround() + ", passenger=" + mockPlayer.isPassenger() + ")"
                ));
                return;
            }
            context.succeed();
        });
    }

    public static void testVehicleRidingMinecartNoAffinity(GameTestHelper context) {
        runAdvancedScenario(context, false, true, "minecart", 0, false, Blocks.STONE, 0.19f, 0.21f, "minecart_no_affinity");
    }
    public static void testVehicleRidingMinecartYesAffinity(GameTestHelper context) {
        runAdvancedScenario(context, true, true, "minecart", 0, false, Blocks.STONE, 0.99f, 1.01f, "minecart_yes_affinity");
    }
    public static void testVehicleRidingHorseNoAffinity(GameTestHelper context) {
        runAdvancedScenario(context, false, true, "horse", 0, false, Blocks.STONE, 0.19f, 0.21f, "horse_no_affinity");
    }
    public static void testVehicleRidingHorseYesAffinity(GameTestHelper context) {
        runAdvancedScenario(context, true, true, "horse", 0, false, Blocks.STONE, 0.99f, 1.01f, "horse_yes_affinity");
    }
    
    public static void testFatigueGroundedNoAffinity(GameTestHelper context) {
        runAdvancedScenario(context, false, false, "none", 1, false, Blocks.STONE, 0.29f, 0.31f, "fatigue1_grounded_no");
    }
    public static void testFatigueGroundedYesAffinity(GameTestHelper context) {
        runAdvancedScenario(context, true, false, "none", 1, false, Blocks.STONE, 0.29f, 0.31f, "fatigue1_grounded_yes");
    }
    public static void testFatigueAirborneNoAffinity(GameTestHelper context) {
        runAdvancedScenario(context, false, true, "none", 1, false, Blocks.STONE, 0.05f, 0.07f, "fatigue1_air_no");
    }
    public static void testFatigueAirborneYesAffinity(GameTestHelper context) {
        runAdvancedScenario(context, true, true, "none", 1, false, Blocks.STONE, 0.29f, 0.31f, "fatigue1_air_yes");
    }
    public static void testFatigue3AirborneNoAffinity(GameTestHelper context) {
        runAdvancedScenario(context, false, true, "none", 3, false, Blocks.STONE, 0.0005f, 0.0006f, "fatigue3_air_no");
    }
    public static void testFatigue3AirborneYesAffinity(GameTestHelper context) {
        runAdvancedScenario(context, true, true, "none", 3, false, Blocks.STONE, 0.0026f, 0.0028f, "fatigue3_air_yes");
    }

    public static void testWrongToolGroundedNoAffinity(GameTestHelper context) {
        runAdvancedScenario(context, false, false, "none", 0, false, Blocks.STONE, 0.99f, 1.01f, "wrongtool_grounded_no");
    }
    public static void testWrongToolGroundedYesAffinity(GameTestHelper context) {
        runAdvancedScenario(context, true, false, "none", 0, false, Blocks.STONE, 0.99f, 1.01f, "wrongtool_grounded_yes");
    }
    public static void testWrongToolAirborneNoAffinity(GameTestHelper context) {
        runAdvancedScenario(context, false, true, "none", 0, false, Blocks.STONE, 0.19f, 0.21f, "wrongtool_air_no");
    }
    public static void testWrongToolAirborneYesAffinity(GameTestHelper context) {
        runAdvancedScenario(context, true, true, "none", 0, false, Blocks.STONE, 0.99f, 1.01f, "wrongtool_air_yes");
    }

    public static void testCobwebAirborneNoAffinity(GameTestHelper context) {
        runAdvancedScenario(context, false, true, "none", 0, true, Blocks.COBWEB, 0.19f, 0.21f, "cobweb_air_no");
    }
    public static void testCobwebAirborneYesAffinity(GameTestHelper context) {
        runAdvancedScenario(context, true, true, "none", 0, true, Blocks.COBWEB, 0.99f, 1.01f, "cobweb_air_yes");
    }

    public static void testFlightAffinitySpawnsProperly(GameTestHelper context) {
        var registry = context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        var enchantmentEntry = registry.getOrThrow(ModEnchantments.FLIGHT_AFFINITY);

        if (!enchantmentEntry.is(EnchantmentTags.TRADEABLE)) {
            context.fail(Component.literal("Flight Affinity is missing the TRADEABLE tag, villagers won't sell it!"));
            return;
        }

        if (!enchantmentEntry.is(EnchantmentTags.ON_RANDOM_LOOT)) {
            context.fail(Component.literal("Flight Affinity is missing the ON_RANDOM_LOOT tag, it won't appear in chests!"));
            return;
        }

        if (!enchantmentEntry.is(EnchantmentTags.IN_ENCHANTING_TABLE)) {
            context.fail(Component.literal("Flight Affinity is missing the IN_ENCHANTING_TABLE tag!"));
            return;
        }

        context.succeed();
    }
    @Test
    void airGroundedAlwaysStaysAt100() {
        assertEquals(100, MiningSpeedRules.finalMiningSpeedPercent(AIR, GROUNDED, false, false));
        assertEquals(100, MiningSpeedRules.finalMiningSpeedPercent(AIR, GROUNDED, true, true));
    }

    @Test
    void airAirborneWithoutFlightAffinityIs20() {
        assertEquals(20, MiningSpeedRules.finalMiningSpeedPercent(AIR, AIRBORNE, false, false));
        assertEquals(20, MiningSpeedRules.finalMiningSpeedPercent(AIR, AIRBORNE, true, false));
    }

    @Test
    void airAirborneWithFlightAffinityIs100() {
        assertEquals(100, MiningSpeedRules.finalMiningSpeedPercent(AIR, AIRBORNE, false, true));
        assertEquals(100, MiningSpeedRules.finalMiningSpeedPercent(AIR, AIRBORNE, true, true));
    }

    @Test
    void waterGroundedWithoutAquaAffinityIs20() {
        assertEquals(20, MiningSpeedRules.finalMiningSpeedPercent(WATER, GROUNDED, false, false));
        assertEquals(20, MiningSpeedRules.finalMiningSpeedPercent(WATER, GROUNDED, false, true));
    }

    @Test
    void waterGroundedWithAquaAffinityIs100() {
        assertEquals(100, MiningSpeedRules.finalMiningSpeedPercent(WATER, GROUNDED, true, false));
        assertEquals(100, MiningSpeedRules.finalMiningSpeedPercent(WATER, GROUNDED, true, true));
    }

    @Test
    void waterFloatingWithoutEnchantsIs4() {
        assertEquals(4, MiningSpeedRules.finalMiningSpeedPercent(WATER, AIRBORNE, false, false));
    }

    @Test
    void waterFloatingWithOneRelevantEnchantIs20() {
        assertEquals(20, MiningSpeedRules.finalMiningSpeedPercent(WATER, AIRBORNE, true, false));
        assertEquals(20, MiningSpeedRules.finalMiningSpeedPercent(WATER, AIRBORNE, false, true));
    }

    @Test
    void waterFloatingWithBothEnchantsIs100() {
        assertEquals(100, MiningSpeedRules.finalMiningSpeedPercent(WATER, AIRBORNE, true, true));
    }

    @Test
    void nonWaterFluidMatchesLavaAndModdedRows() {
        assertEquals(100, MiningSpeedRules.finalMiningSpeedPercent(OTHER_FLUID, GROUNDED, false, false));
        assertEquals(100, MiningSpeedRules.finalMiningSpeedPercent(OTHER_FLUID, GROUNDED, true, true));
        assertEquals(20, MiningSpeedRules.finalMiningSpeedPercent(OTHER_FLUID, AIRBORNE, false, false));
        assertEquals(20, MiningSpeedRules.finalMiningSpeedPercent(OTHER_FLUID, AIRBORNE, true, false));
        assertEquals(100, MiningSpeedRules.finalMiningSpeedPercent(OTHER_FLUID, AIRBORNE, false, true));
        assertEquals(100, MiningSpeedRules.finalMiningSpeedPercent(OTHER_FLUID, AIRBORNE, true, true));
    }
}
