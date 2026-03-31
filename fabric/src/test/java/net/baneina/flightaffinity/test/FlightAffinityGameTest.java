package net.baneina.flightaffinity.test;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public class FlightAffinityGameTest implements ModInitializer {

    @Override
    public void onInitialize() {
        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_air_grounded_flight_only"),
            (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityAirGroundedFlightOnly
        );

        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_air_grounded_both"),
            (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityAirGroundedBoth
        );

        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_air_airborne_flight_only"),
            (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityAirAirborneFlightOnly
        );

        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_air_airborne_both"),
            (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityAirAirborneBoth
        );

        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_water_grounded_flight_only"),
            (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityWaterGroundedFlightOnly
        );

        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_water_grounded_both"),
            (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityWaterGroundedBoth
        );

        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_water_floating_flight_only"),
            (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityWaterFloatingFlightOnly
        );

        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_water_floating_both"),
            (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityWaterFloatingBoth
        );

        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_lava_grounded_flight_only"),
            (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityLavaGroundedFlightOnly
        );

        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_lava_grounded_both"),
            (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityLavaGroundedBoth
        );

        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_lava_floating_flight_only"),
            (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityLavaFloatingFlightOnly
        );

        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_lava_floating_both"),
            (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityLavaFloatingBoth
        );

        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_air_airborne_no_affinity"),
            (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityAirAirborneNoAffinity
        );

        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_water_floating_no_affinity"),
            (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityWaterFloatingNoAffinity
        );

        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_air_airborne_unequip_affinity"),
            (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinityAirAirborneUnequipAffinity
        );

        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_spawns_properly"),
            (Consumer<GameTestHelper>) CommonGameTests::testFlightAffinitySpawnsProperly
        );

        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_vehicle_riding_minecart_no_affinity"),
            (Consumer<GameTestHelper>) CommonGameTests::testVehicleRidingMinecartNoAffinity
        );
        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_vehicle_riding_minecart_yes_affinity"),
            (Consumer<GameTestHelper>) CommonGameTests::testVehicleRidingMinecartYesAffinity
        );
        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_vehicle_riding_horse_no_affinity"),
            (Consumer<GameTestHelper>) CommonGameTests::testVehicleRidingHorseNoAffinity
        );
        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_vehicle_riding_horse_yes_affinity"),
            (Consumer<GameTestHelper>) CommonGameTests::testVehicleRidingHorseYesAffinity
        );
        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_fatigue_grounded_no_affinity"),
            (Consumer<GameTestHelper>) CommonGameTests::testFatigueGroundedNoAffinity
        );
        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_fatigue_grounded_yes_affinity"),
            (Consumer<GameTestHelper>) CommonGameTests::testFatigueGroundedYesAffinity
        );
        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_fatigue_airborne_no_affinity"),
            (Consumer<GameTestHelper>) CommonGameTests::testFatigueAirborneNoAffinity
        );
        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_fatigue_airborne_yes_affinity"),
            (Consumer<GameTestHelper>) CommonGameTests::testFatigueAirborneYesAffinity
        );
        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_fatigue3_airborne_no_affinity"),
            (Consumer<GameTestHelper>) CommonGameTests::testFatigue3AirborneNoAffinity
        );
        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_fatigue3_airborne_yes_affinity"),
            (Consumer<GameTestHelper>) CommonGameTests::testFatigue3AirborneYesAffinity
        );
        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_wrong_tool_grounded_no_affinity"),
            (Consumer<GameTestHelper>) CommonGameTests::testWrongToolGroundedNoAffinity
        );
        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_wrong_tool_grounded_yes_affinity"),
            (Consumer<GameTestHelper>) CommonGameTests::testWrongToolGroundedYesAffinity
        );
        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_wrong_tool_airborne_no_affinity"),
            (Consumer<GameTestHelper>) CommonGameTests::testWrongToolAirborneNoAffinity
        );
        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_wrong_tool_airborne_yes_affinity"),
            (Consumer<GameTestHelper>) CommonGameTests::testWrongToolAirborneYesAffinity
        );
        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_cobweb_airborne_no_affinity"),
            (Consumer<GameTestHelper>) CommonGameTests::testCobwebAirborneNoAffinity
        );
        Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION,
            Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_cobweb_airborne_yes_affinity"),
            (Consumer<GameTestHelper>) CommonGameTests::testCobwebAirborneYesAffinity
        );
    }
}
