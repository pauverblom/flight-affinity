package net.baneina.flightaffinity.test;

import net.minecraft.gametest.framework.GameTestHelper;

import static net.baneina.flightaffinity.test.FlightAffinityTestConstants.*;

/**
 * In-game verification of the 24-case mining-speed penalty matrix.
 *
 * <p>Axes: 3 environments (AIR, WATER, LAVA) × 2 stances (GROUNDED, AIRBORNE)
 * × 2 Aqua Affinity × 2 Flight Affinity = 24 combinations.
 */
public class FlightAffinityEnvironmentTests {

    // -- AIR × GROUNDED -------------------------------------------------------

    public static void testAirGroundedNone(GameTestHelper ctx) {
        new GameTestScenarioBuilder(ctx).setAirborne(false)
                .expectedSpeed(NO_PENALTY_MIN, NO_PENALTY_MAX).name("air_grounded_none").run();
    }

    public static void testAirGroundedAqua(GameTestHelper ctx) {
        new GameTestScenarioBuilder(ctx).withAquaAffinity().setAirborne(false)
                .expectedSpeed(NO_PENALTY_MIN, NO_PENALTY_MAX).name("air_grounded_aqua").run();
    }

    public static void testAirGroundedFlight(GameTestHelper ctx) {
        new GameTestScenarioBuilder(ctx).withFlightAffinity().setAirborne(false)
                .expectedSpeed(NO_PENALTY_MIN, NO_PENALTY_MAX).name("air_grounded_flight").run();
    }

    public static void testAirGroundedBoth(GameTestHelper ctx) {
        new GameTestScenarioBuilder(ctx).withFlightAffinity().withAquaAffinity().setAirborne(false)
                .expectedSpeed(NO_PENALTY_MIN, NO_PENALTY_MAX).name("air_grounded_both").run();
    }

    // -- AIR × AIRBORNE -------------------------------------------------------

    public static void testAirAirborneNone(GameTestHelper ctx) {
        new GameTestScenarioBuilder(ctx).setAirborne(true)
                .expectedSpeed(SINGLE_PENALTY_MIN, SINGLE_PENALTY_MAX).name("air_airborne_none").run();
    }

    public static void testAirAirborneAqua(GameTestHelper ctx) {
        new GameTestScenarioBuilder(ctx).withAquaAffinity().setAirborne(true)
                .expectedSpeed(SINGLE_PENALTY_MIN, SINGLE_PENALTY_MAX).name("air_airborne_aqua").run();
    }

    public static void testAirAirborneFlight(GameTestHelper ctx) {
        new GameTestScenarioBuilder(ctx).withFlightAffinity().setAirborne(true)
                .expectedSpeed(NO_PENALTY_MIN, NO_PENALTY_MAX).name("air_airborne_flight").run();
    }

    public static void testAirAirborneBoth(GameTestHelper ctx) {
        new GameTestScenarioBuilder(ctx).withFlightAffinity().withAquaAffinity().setAirborne(true)
                .expectedSpeed(NO_PENALTY_MIN, NO_PENALTY_MAX).name("air_airborne_both").run();
    }

    // -- WATER × GROUNDED -----------------------------------------------------

    public static void testWaterGroundedNone(GameTestHelper ctx) {
        new GameTestScenarioBuilder(ctx).inWater().setAirborne(false)
                .expectedSpeed(SINGLE_PENALTY_MIN, SINGLE_PENALTY_MAX).name("water_grounded_none").run();
    }

    public static void testWaterGroundedAqua(GameTestHelper ctx) {
        new GameTestScenarioBuilder(ctx).withAquaAffinity().inWater().setAirborne(false)
                .expectedSpeed(NO_PENALTY_MIN, NO_PENALTY_MAX).name("water_grounded_aqua").run();
    }

    public static void testWaterGroundedFlight(GameTestHelper ctx) {
        new GameTestScenarioBuilder(ctx).withFlightAffinity().inWater().setAirborne(false)
                .expectedSpeed(SINGLE_PENALTY_MIN, SINGLE_PENALTY_MAX).name("water_grounded_flight").run();
    }

    public static void testWaterGroundedBoth(GameTestHelper ctx) {
        new GameTestScenarioBuilder(ctx).withFlightAffinity().withAquaAffinity().inWater().setAirborne(false)
                .expectedSpeed(NO_PENALTY_MIN, NO_PENALTY_MAX).name("water_grounded_both").run();
    }

    // -- WATER × AIRBORNE -----------------------------------------------------

    public static void testWaterAirborneNone(GameTestHelper ctx) {
        new GameTestScenarioBuilder(ctx).inWater().setAirborne(true)
                .expectedSpeed(DOUBLE_PENALTY_MIN, DOUBLE_PENALTY_MAX).name("water_airborne_none").run();
    }

    public static void testWaterAirborneAqua(GameTestHelper ctx) {
        new GameTestScenarioBuilder(ctx).withAquaAffinity().inWater().setAirborne(true)
                .expectedSpeed(SINGLE_PENALTY_MIN, SINGLE_PENALTY_MAX).name("water_airborne_aqua").run();
    }

    public static void testWaterAirborneFlight(GameTestHelper ctx) {
        new GameTestScenarioBuilder(ctx).withFlightAffinity().inWater().setAirborne(true)
                .expectedSpeed(SINGLE_PENALTY_MIN, SINGLE_PENALTY_MAX).name("water_airborne_flight").run();
    }

    public static void testWaterAirborneBoth(GameTestHelper ctx) {
        new GameTestScenarioBuilder(ctx).withFlightAffinity().withAquaAffinity().inWater().setAirborne(true)
                .expectedSpeed(NO_PENALTY_MIN, NO_PENALTY_MAX).name("water_airborne_both").run();
    }

    // -- LAVA × GROUNDED ------------------------------------------------------

    public static void testLavaGroundedNone(GameTestHelper ctx) {
        new GameTestScenarioBuilder(ctx).inLava().setAirborne(false)
                .expectedSpeed(NO_PENALTY_MIN, NO_PENALTY_MAX).name("lava_grounded_none").run();
    }

    public static void testLavaGroundedAqua(GameTestHelper ctx) {
        new GameTestScenarioBuilder(ctx).withAquaAffinity().inLava().setAirborne(false)
                .expectedSpeed(NO_PENALTY_MIN, NO_PENALTY_MAX).name("lava_grounded_aqua").run();
    }

    public static void testLavaGroundedFlight(GameTestHelper ctx) {
        new GameTestScenarioBuilder(ctx).withFlightAffinity().inLava().setAirborne(false)
                .expectedSpeed(NO_PENALTY_MIN, NO_PENALTY_MAX).name("lava_grounded_flight").run();
    }

    public static void testLavaGroundedBoth(GameTestHelper ctx) {
        new GameTestScenarioBuilder(ctx).withFlightAffinity().withAquaAffinity().inLava().setAirborne(false)
                .expectedSpeed(NO_PENALTY_MIN, NO_PENALTY_MAX).name("lava_grounded_both").run();
    }

    // -- LAVA × AIRBORNE ------------------------------------------------------

    public static void testLavaAirborneNone(GameTestHelper ctx) {
        new GameTestScenarioBuilder(ctx).inLava().setAirborne(true)
                .expectedSpeed(SINGLE_PENALTY_MIN, SINGLE_PENALTY_MAX).name("lava_airborne_none").run();
    }

    public static void testLavaAirborneAqua(GameTestHelper ctx) {
        new GameTestScenarioBuilder(ctx).withAquaAffinity().inLava().setAirborne(true)
                .expectedSpeed(SINGLE_PENALTY_MIN, SINGLE_PENALTY_MAX).name("lava_airborne_aqua").run();
    }

    public static void testLavaAirborneFlight(GameTestHelper ctx) {
        new GameTestScenarioBuilder(ctx).withFlightAffinity().inLava().setAirborne(true)
                .expectedSpeed(NO_PENALTY_MIN, NO_PENALTY_MAX).name("lava_airborne_flight").run();
    }

    public static void testLavaAirborneBoth(GameTestHelper ctx) {
        new GameTestScenarioBuilder(ctx).withFlightAffinity().withAquaAffinity().inLava().setAirborne(true)
                .expectedSpeed(NO_PENALTY_MIN, NO_PENALTY_MAX).name("lava_airborne_both").run();
    }
}
