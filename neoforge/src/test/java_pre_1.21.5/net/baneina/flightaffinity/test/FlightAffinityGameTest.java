package net.baneina.flightaffinity.test;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * NeoForge game-test class for pre-1.21.5 (annotation-based discovery via {@code @GameTestHolder}).
 */
@GameTestHolder("flightaffinity-test")
@PrefixGameTestTemplate(false)
public class FlightAffinityGameTest {

    // In NeoForge 21.0.x the template field is a plain path; the namespace is
    // automatically prepended from @GameTestHolder.  Using "minecraft:empty"
    // would produce the invalid compound "flightaffinity-test:minecraft:empty".
    // We therefore declare our own 1×1×1 empty structure under the test namespace.
    private static final String EMPTY = "empty";

    // -- Environment: AIR × GROUNDED ------------------------------------------

    @GameTest(template = EMPTY)
    public static void airGroundedNone(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testAirGroundedNone(ctx);
    }

    @GameTest(template = EMPTY)
    public static void airGroundedAqua(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testAirGroundedAqua(ctx);
    }

    @GameTest(template = EMPTY)
    public static void airGroundedFlight(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testAirGroundedFlight(ctx);
    }

    @GameTest(template = EMPTY)
    public static void airGroundedBoth(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testAirGroundedBoth(ctx);
    }

    // -- Environment: AIR × AIRBORNE ------------------------------------------

    @GameTest(template = EMPTY)
    public static void airAirborneNone(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testAirAirborneNone(ctx);
    }

    @GameTest(template = EMPTY)
    public static void airAirborneAqua(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testAirAirborneAqua(ctx);
    }

    @GameTest(template = EMPTY)
    public static void airAirborneFlight(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testAirAirborneFlight(ctx);
    }

    @GameTest(template = EMPTY)
    public static void airAirborneBoth(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testAirAirborneBoth(ctx);
    }

    // -- Environment: WATER × GROUNDED ----------------------------------------

    @GameTest(template = EMPTY)
    public static void waterGroundedNone(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testWaterGroundedNone(ctx);
    }

    @GameTest(template = EMPTY)
    public static void waterGroundedAqua(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testWaterGroundedAqua(ctx);
    }

    @GameTest(template = EMPTY)
    public static void waterGroundedFlight(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testWaterGroundedFlight(ctx);
    }

    @GameTest(template = EMPTY)
    public static void waterGroundedBoth(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testWaterGroundedBoth(ctx);
    }

    // -- Environment: WATER × AIRBORNE ----------------------------------------

    @GameTest(template = EMPTY)
    public static void waterAirborneNone(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testWaterAirborneNone(ctx);
    }

    @GameTest(template = EMPTY)
    public static void waterAirborneAqua(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testWaterAirborneAqua(ctx);
    }

    @GameTest(template = EMPTY)
    public static void waterAirborneFlight(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testWaterAirborneFlight(ctx);
    }

    @GameTest(template = EMPTY)
    public static void waterAirborneBoth(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testWaterAirborneBoth(ctx);
    }

    // -- Environment: LAVA × GROUNDED -----------------------------------------

    @GameTest(template = EMPTY)
    public static void lavaGroundedNone(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testLavaGroundedNone(ctx);
    }

    @GameTest(template = EMPTY)
    public static void lavaGroundedAqua(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testLavaGroundedAqua(ctx);
    }

    @GameTest(template = EMPTY)
    public static void lavaGroundedFlight(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testLavaGroundedFlight(ctx);
    }

    @GameTest(template = EMPTY)
    public static void lavaGroundedBoth(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testLavaGroundedBoth(ctx);
    }

    // -- Environment: LAVA × AIRBORNE -----------------------------------------

    @GameTest(template = EMPTY)
    public static void lavaAirborneNone(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testLavaAirborneNone(ctx);
    }

    @GameTest(template = EMPTY)
    public static void lavaAirborneAqua(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testLavaAirborneAqua(ctx);
    }

    @GameTest(template = EMPTY)
    public static void lavaAirborneFlight(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testLavaAirborneFlight(ctx);
    }

    @GameTest(template = EMPTY)
    public static void lavaAirborneBoth(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testLavaAirborneBoth(ctx);
    }

    // -- Mechanics ------------------------------------------------------------

    @GameTest(template = EMPTY)
    public static void unequipAffinity(GameTestHelper ctx) {
        FlightAffinityMechanicsTests.testFlightAffinityAirAirborneUnequipAffinity(ctx);
    }

    // -- Registry -------------------------------------------------------------

    @GameTest(template = EMPTY)
    public static void spawnsProperly(GameTestHelper ctx) {
        FlightAffinityRegistryTests.testFlightAffinitySpawnsProperly(ctx);
    }

    // -- Vehicles -------------------------------------------------------------

    @GameTest(template = EMPTY)
    public static void vehicleMinecartNo(GameTestHelper ctx) {
        FlightAffinityVehicleTests.testVehicleRidingMinecartNoAffinity(ctx);
    }

    @GameTest(template = EMPTY)
    public static void vehicleMinecartYes(GameTestHelper ctx) {
        FlightAffinityVehicleTests.testVehicleRidingMinecartYesAffinity(ctx);
    }

    @GameTest(template = EMPTY)
    public static void vehicleHorseNo(GameTestHelper ctx) {
        FlightAffinityVehicleTests.testVehicleRidingHorseNoAffinity(ctx);
    }

    @GameTest(template = EMPTY)
    public static void vehicleHorseYes(GameTestHelper ctx) {
        FlightAffinityVehicleTests.testVehicleRidingHorseYesAffinity(ctx);
    }
}

