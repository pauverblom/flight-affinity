package net.baneina.flightaffinity.test.fabric;

import net.baneina.flightaffinity.test.FlightAffinityTests;
//? if >=26.1 {
import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

import java.lang.reflect.Method;
//?} else if >=1.21.5 {
/*import net.baneina.flightaffinity.FlightAffinity;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
*///?} else {
/*import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;

import java.util.Collection;
*///?}

// Registered through src/test/resources/fabric.mod.json (a separate "flightaffinity-test" mod).
//? if >=26.1 {
// On 26.x the test function registry is already frozen when mod initializers run, so tests go
// through Fabric API's @GameTest annotations. Each stub forwards to FlightAffinityTests.ALL by name;
// CI fails if fewer tests pass than there are entries, so a missing stub does not go unnoticed.
public class FabricGameTests implements CustomTestMethodInvoker {
    @Override
    public void invokeTestMethod(GameTestHelper helper, Method method) {
        FlightAffinityTests.ALL.get(method.getName()).accept(helper);
    }

    @GameTest(maxTicks = 100) public void air_grounded_none(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void air_grounded_flight(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void air_grounded_aqua(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void air_grounded_both(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void air_airborne_none(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void air_airborne_flight(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void air_airborne_aqua(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void air_airborne_both(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void water_grounded_none(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void water_grounded_flight(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void water_grounded_aqua(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void water_grounded_both(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void water_airborne_none(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void water_airborne_flight(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void water_airborne_aqua(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void water_airborne_both(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void lava_grounded_none(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void lava_grounded_flight(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void lava_grounded_aqua(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void lava_grounded_both(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void lava_airborne_none(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void lava_airborne_flight(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void lava_airborne_aqua(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void lava_airborne_both(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void vehicle_minecart_no(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void vehicle_minecart_yes(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void vehicle_horse_no(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void vehicle_horse_yes(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void unequip_affinity(GameTestHelper helper) {}
    @GameTest(maxTicks = 100) public void spawns_properly(GameTestHelper helper) {}
}
//?} else if >=1.21.5 {
/*public class FabricGameTests implements ModInitializer {
    @Override
    public void onInitialize() {
        FlightAffinityTests.ALL.forEach((name, test) -> Registry.register(BuiltInRegistries.TEST_FUNCTION,
                FlightAffinity.id(FlightAffinityTests.NAMESPACE, name), test));
    }
}
*///?} else {
/*public class FabricGameTests {
    @GameTestGenerator
    public Collection<TestFunction> tests() {
        return FlightAffinityTests.testFunctions();
    }
}
*///?}
