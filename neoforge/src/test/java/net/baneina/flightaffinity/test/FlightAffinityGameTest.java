package net.baneina.flightaffinity.test;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.Consumer;

@EventBusSubscriber(modid = "flightaffinity")
public class FlightAffinityGameTest {

    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key())) {

            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_air_grounded_flight_only"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityAirGroundedFlightOnly
            );

            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_air_grounded_both"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityAirGroundedBoth
            );

            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_air_airborne_flight_only"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityAirAirborneFlightOnly
            );

            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_air_airborne_both"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityAirAirborneBoth
            );

            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_water_grounded_flight_only"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityWaterGroundedFlightOnly
            );

            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_water_grounded_both"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityWaterGroundedBoth
            );

            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_water_floating_flight_only"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityWaterFloatingFlightOnly
            );

            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_water_floating_both"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityWaterFloatingBoth
            );

            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_lava_grounded_flight_only"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityLavaGroundedFlightOnly
            );

            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_lava_grounded_both"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityLavaGroundedBoth
            );

            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_lava_floating_flight_only"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityLavaFloatingFlightOnly
            );

            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_lava_floating_both"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityLavaFloatingBoth
            );

            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_air_airborne_no_affinity"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityAirAirborneNoAffinity
            );

            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_water_floating_no_affinity"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityWaterFloatingNoAffinity
            );

            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_air_airborne_unequip_affinity"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityAirAirborneUnequipAffinity
            );

            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_spawns_properly"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinitySpawnsProperly
            );

            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_vehicle_riding_minecart_no_affinity"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testVehicleRidingMinecartNoAffinity
            );
            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_vehicle_riding_minecart_yes_affinity"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testVehicleRidingMinecartYesAffinity
            );
            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_vehicle_riding_horse_no_affinity"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testVehicleRidingHorseNoAffinity
            );
            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_vehicle_riding_horse_yes_affinity"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testVehicleRidingHorseYesAffinity
            );
            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_fatigue_grounded_no_affinity"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testFatigueGroundedNoAffinity
            );
            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_fatigue_grounded_yes_affinity"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testFatigueGroundedYesAffinity
            );
            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_fatigue_airborne_no_affinity"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testFatigueAirborneNoAffinity
            );
            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_fatigue_airborne_yes_affinity"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testFatigueAirborneYesAffinity
            );
            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_fatigue3_airborne_no_affinity"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testFatigue3AirborneNoAffinity
            );
            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_fatigue3_airborne_yes_affinity"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testFatigue3AirborneYesAffinity
            );
            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_wrong_tool_grounded_no_affinity"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testWrongToolGroundedNoAffinity
            );
            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_wrong_tool_grounded_yes_affinity"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testWrongToolGroundedYesAffinity
            );
            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_wrong_tool_airborne_no_affinity"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testWrongToolAirborneNoAffinity
            );
            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_wrong_tool_airborne_yes_affinity"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testWrongToolAirborneYesAffinity
            );
            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_cobweb_airborne_no_affinity"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testCobwebAirborneNoAffinity
            );
            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_cobweb_airborne_yes_affinity"),
                    () -> (Consumer<GameTestHelper>) CommonGameTests::testCobwebAirborneYesAffinity
            );
        }
    }
}
