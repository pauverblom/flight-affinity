package net.baneina.flightaffinity.test;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public class FlightAffinityGameTest implements ModInitializer {

    private static final String NS = "flightaffinity-test";

    @Override
    public void onInitialize() {
        // 24-case penalty matrix
        register("air_grounded_none",    FlightAffinityEnvironmentTests::testAirGroundedNone);
        register("air_grounded_aqua",    FlightAffinityEnvironmentTests::testAirGroundedAqua);
        register("air_grounded_flight",  FlightAffinityEnvironmentTests::testAirGroundedFlight);
        register("air_grounded_both",    FlightAffinityEnvironmentTests::testAirGroundedBoth);
        register("air_airborne_none",    FlightAffinityEnvironmentTests::testAirAirborneNone);
        register("air_airborne_aqua",    FlightAffinityEnvironmentTests::testAirAirborneAqua);
        register("air_airborne_flight",  FlightAffinityEnvironmentTests::testAirAirborneFlight);
        register("air_airborne_both",    FlightAffinityEnvironmentTests::testAirAirborneBoth);
        register("water_grounded_none",  FlightAffinityEnvironmentTests::testWaterGroundedNone);
        register("water_grounded_aqua",  FlightAffinityEnvironmentTests::testWaterGroundedAqua);
        register("water_grounded_flight",FlightAffinityEnvironmentTests::testWaterGroundedFlight);
        register("water_grounded_both",  FlightAffinityEnvironmentTests::testWaterGroundedBoth);
        register("water_airborne_none",  FlightAffinityEnvironmentTests::testWaterAirborneNone);
        register("water_airborne_aqua",  FlightAffinityEnvironmentTests::testWaterAirborneAqua);
        register("water_airborne_flight",FlightAffinityEnvironmentTests::testWaterAirborneFlight);
        register("water_airborne_both",  FlightAffinityEnvironmentTests::testWaterAirborneBoth);
        register("lava_grounded_none",   FlightAffinityEnvironmentTests::testLavaGroundedNone);
        register("lava_grounded_aqua",   FlightAffinityEnvironmentTests::testLavaGroundedAqua);
        register("lava_grounded_flight", FlightAffinityEnvironmentTests::testLavaGroundedFlight);
        register("lava_grounded_both",   FlightAffinityEnvironmentTests::testLavaGroundedBoth);
        register("lava_airborne_none",   FlightAffinityEnvironmentTests::testLavaAirborneNone);
        register("lava_airborne_aqua",   FlightAffinityEnvironmentTests::testLavaAirborneAqua);
        register("lava_airborne_flight", FlightAffinityEnvironmentTests::testLavaAirborneFlight);
        register("lava_airborne_both",   FlightAffinityEnvironmentTests::testLavaAirborneBoth);

        // Mechanics
        register("unequip_affinity", FlightAffinityMechanicsTests::testFlightAffinityAirAirborneUnequipAffinity);

        // Registry
        register("spawns_properly", FlightAffinityRegistryTests::testFlightAffinitySpawnsProperly);

        // Vehicles
        register("vehicle_minecart_no", FlightAffinityVehicleTests::testVehicleRidingMinecartNoAffinity);
        register("vehicle_minecart_yes", FlightAffinityVehicleTests::testVehicleRidingMinecartYesAffinity);
        register("vehicle_horse_no", FlightAffinityVehicleTests::testVehicleRidingHorseNoAffinity);
        register("vehicle_horse_yes", FlightAffinityVehicleTests::testVehicleRidingHorseYesAffinity);
    }

    private static void register(String name, Consumer<GameTestHelper> test) {
        Registry.register(
                net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
                Identifier.fromNamespaceAndPath(NS, name),
                test
        );
    }
}
