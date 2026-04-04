package net.baneina.flightaffinity.test;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

/**
 * Fabric game-test entrypoint for pre-1.21.5 (annotation-based discovery).
 * Registered via the {@code fabric-gametest} entrypoint in fabric.mod.json.
 */
public class FlightAffinityGameTest {

    private static final String EMPTY = FabricGameTest.EMPTY_STRUCTURE;

    // -- Environment: AIR × GROUNDED ------------------------------------------

    @GameTest(template = EMPTY)
    public void airGroundedNone(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testAirGroundedNone(ctx);
    }

    @GameTest(template = EMPTY)
    public void airGroundedAqua(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testAirGroundedAqua(ctx);
    }

    @GameTest(template = EMPTY)
    public void airGroundedFlight(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testAirGroundedFlight(ctx);
    }

    @GameTest(template = EMPTY)
    public void airGroundedBoth(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testAirGroundedBoth(ctx);
    }

    // -- Environment: AIR × AIRBORNE ------------------------------------------

    @GameTest(template = EMPTY)
    public void airAirborneNone(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testAirAirborneNone(ctx);
    }

    @GameTest(template = EMPTY)
    public void airAirborneAqua(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testAirAirborneAqua(ctx);
    }

    @GameTest(template = EMPTY)
    public void airAirborneFlight(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testAirAirborneFlight(ctx);
    }

    @GameTest(template = EMPTY)
    public void airAirborneBoth(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testAirAirborneBoth(ctx);
    }

    // -- Environment: WATER × GROUNDED ----------------------------------------

    @GameTest(template = EMPTY)
    public void waterGroundedNone(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testWaterGroundedNone(ctx);
    }

    @GameTest(template = EMPTY)
    public void waterGroundedAqua(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testWaterGroundedAqua(ctx);
    }

    @GameTest(template = EMPTY)
    public void waterGroundedFlight(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testWaterGroundedFlight(ctx);
    }

    @GameTest(template = EMPTY)
    public void waterGroundedBoth(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testWaterGroundedBoth(ctx);
    }

    // -- Environment: WATER × AIRBORNE ----------------------------------------

    @GameTest(template = EMPTY)
    public void waterAirborneNone(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testWaterAirborneNone(ctx);
    }

    @GameTest(template = EMPTY)
    public void waterAirborneAqua(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testWaterAirborneAqua(ctx);
    }

    @GameTest(template = EMPTY)
    public void waterAirborneFlight(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testWaterAirborneFlight(ctx);
    }

    @GameTest(template = EMPTY)
    public void waterAirborneBoth(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testWaterAirborneBoth(ctx);
    }

    // -- Environment: LAVA × GROUNDED -----------------------------------------

    @GameTest(template = EMPTY)
    public void lavaGroundedNone(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testLavaGroundedNone(ctx);
    }

    @GameTest(template = EMPTY)
    public void lavaGroundedAqua(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testLavaGroundedAqua(ctx);
    }

    @GameTest(template = EMPTY)
    public void lavaGroundedFlight(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testLavaGroundedFlight(ctx);
    }

    @GameTest(template = EMPTY)
    public void lavaGroundedBoth(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testLavaGroundedBoth(ctx);
    }

    // -- Environment: LAVA × AIRBORNE -----------------------------------------

    @GameTest(template = EMPTY)
    public void lavaAirborneNone(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testLavaAirborneNone(ctx);
    }

    @GameTest(template = EMPTY)
    public void lavaAirborneAqua(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testLavaAirborneAqua(ctx);
    }

    @GameTest(template = EMPTY)
    public void lavaAirborneFlight(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testLavaAirborneFlight(ctx);
    }

    @GameTest(template = EMPTY)
    public void lavaAirborneBoth(GameTestHelper ctx) {
        FlightAffinityEnvironmentTests.testLavaAirborneBoth(ctx);
    }

    // -- Mechanics ------------------------------------------------------------

    @GameTest(template = EMPTY)
    public void unequipAffinity(GameTestHelper ctx) {
        FlightAffinityMechanicsTests.testFlightAffinityAirAirborneUnequipAffinity(ctx);
    }

    // -- Registry -------------------------------------------------------------

    @GameTest(template = EMPTY)
    public void spawnsProperly(GameTestHelper ctx) {
        FlightAffinityRegistryTests.testFlightAffinitySpawnsProperly(ctx);
    }

    // -- Vehicles -------------------------------------------------------------

    @GameTest(template = EMPTY)
    public void vehicleMinecartNo(GameTestHelper ctx) {
        FlightAffinityVehicleTests.testVehicleRidingMinecartNoAffinity(ctx);
    }

    @GameTest(template = EMPTY)
    public void vehicleMinecartYes(GameTestHelper ctx) {
        FlightAffinityVehicleTests.testVehicleRidingMinecartYesAffinity(ctx);
    }

    @GameTest(template = EMPTY)
    public void vehicleHorseNo(GameTestHelper ctx) {
        FlightAffinityVehicleTests.testVehicleRidingHorseNoAffinity(ctx);
    }

    @GameTest(template = EMPTY)
    public void vehicleHorseYes(GameTestHelper ctx) {
        FlightAffinityVehicleTests.testVehicleRidingHorseYesAffinity(ctx);
    }
}

