package net.baneina.flightaffinity.test;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.Consumer;

@EventBusSubscriber(modid = "flightaffinity")
public class FlightAffinityGameTest {

    private static final String NS = "flightaffinity-test";

    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        if (!event.getRegistryKey().equals(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key())) {
            return;
        }

        // 24-case penalty matrix
        register(event, "air_grounded_none",    FlightAffinityEnvironmentTests::testAirGroundedNone);
        register(event, "air_grounded_aqua",    FlightAffinityEnvironmentTests::testAirGroundedAqua);
        register(event, "air_grounded_flight",  FlightAffinityEnvironmentTests::testAirGroundedFlight);
        register(event, "air_grounded_both",    FlightAffinityEnvironmentTests::testAirGroundedBoth);
        register(event, "air_airborne_none",    FlightAffinityEnvironmentTests::testAirAirborneNone);
        register(event, "air_airborne_aqua",    FlightAffinityEnvironmentTests::testAirAirborneAqua);
        register(event, "air_airborne_flight",  FlightAffinityEnvironmentTests::testAirAirborneFlight);
        register(event, "air_airborne_both",    FlightAffinityEnvironmentTests::testAirAirborneBoth);
        register(event, "water_grounded_none",  FlightAffinityEnvironmentTests::testWaterGroundedNone);
        register(event, "water_grounded_aqua",  FlightAffinityEnvironmentTests::testWaterGroundedAqua);
        register(event, "water_grounded_flight",FlightAffinityEnvironmentTests::testWaterGroundedFlight);
        register(event, "water_grounded_both",  FlightAffinityEnvironmentTests::testWaterGroundedBoth);
        register(event, "water_airborne_none",  FlightAffinityEnvironmentTests::testWaterAirborneNone);
        register(event, "water_airborne_aqua",  FlightAffinityEnvironmentTests::testWaterAirborneAqua);
        register(event, "water_airborne_flight",FlightAffinityEnvironmentTests::testWaterAirborneFlight);
        register(event, "water_airborne_both",  FlightAffinityEnvironmentTests::testWaterAirborneBoth);
        register(event, "lava_grounded_none",   FlightAffinityEnvironmentTests::testLavaGroundedNone);
        register(event, "lava_grounded_aqua",   FlightAffinityEnvironmentTests::testLavaGroundedAqua);
        register(event, "lava_grounded_flight", FlightAffinityEnvironmentTests::testLavaGroundedFlight);
        register(event, "lava_grounded_both",   FlightAffinityEnvironmentTests::testLavaGroundedBoth);
        register(event, "lava_airborne_none",   FlightAffinityEnvironmentTests::testLavaAirborneNone);
        register(event, "lava_airborne_aqua",   FlightAffinityEnvironmentTests::testLavaAirborneAqua);
        register(event, "lava_airborne_flight", FlightAffinityEnvironmentTests::testLavaAirborneFlight);
        register(event, "lava_airborne_both",   FlightAffinityEnvironmentTests::testLavaAirborneBoth);

        // Mechanics
        register(event, "unequip_affinity", FlightAffinityMechanicsTests::testFlightAffinityAirAirborneUnequipAffinity);

        // Registry
        register(event, "spawns_properly", FlightAffinityRegistryTests::testFlightAffinitySpawnsProperly);

        // Vehicles
        register(event, "vehicle_minecart_no", FlightAffinityVehicleTests::testVehicleRidingMinecartNoAffinity);
        register(event, "vehicle_minecart_yes", FlightAffinityVehicleTests::testVehicleRidingMinecartYesAffinity);
        register(event, "vehicle_horse_no", FlightAffinityVehicleTests::testVehicleRidingHorseNoAffinity);
        register(event, "vehicle_horse_yes", FlightAffinityVehicleTests::testVehicleRidingHorseYesAffinity);
    }

    private static void register(RegisterEvent event, String name, Consumer<GameTestHelper> test) {
        event.register(
                net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                Identifier.fromNamespaceAndPath(NS, name),
                () -> test
        );
    }
}
