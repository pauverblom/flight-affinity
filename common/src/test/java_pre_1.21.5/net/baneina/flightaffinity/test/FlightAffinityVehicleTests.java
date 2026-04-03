package net.baneina.flightaffinity.test;

import net.minecraft.gametest.framework.GameTestHelper;

import static net.baneina.flightaffinity.test.FlightAffinityTestConstants.*;

public class FlightAffinityVehicleTests {
    public static void testVehicleRidingMinecartNoAffinity(GameTestHelper context) {
        new GameTestScenarioBuilder(context).withVehicle("minecart").setAirborne(true).expectedSpeed(SINGLE_PENALTY_MIN, SINGLE_PENALTY_MAX).name("minecart_no_affinity").run();
    }

    public static void testVehicleRidingMinecartYesAffinity(GameTestHelper context) {
        new GameTestScenarioBuilder(context).withFlightAffinity().withVehicle("minecart").setAirborne(true).expectedSpeed(NO_PENALTY_MIN, NO_PENALTY_MAX).name("minecart_yes_affinity").run();
    }

    public static void testVehicleRidingHorseNoAffinity(GameTestHelper context) {
        new GameTestScenarioBuilder(context).withVehicle("horse").setAirborne(true).expectedSpeed(SINGLE_PENALTY_MIN, SINGLE_PENALTY_MAX).name("horse_no_affinity").run();
    }

    public static void testVehicleRidingHorseYesAffinity(GameTestHelper context) {
        new GameTestScenarioBuilder(context).withFlightAffinity().withVehicle("horse").setAirborne(true).expectedSpeed(NO_PENALTY_MIN, NO_PENALTY_MAX).name("horse_yes_affinity").run();
    }
}

