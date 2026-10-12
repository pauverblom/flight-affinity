package net.baneina.flightaffinity.test.fabric;

import net.baneina.flightaffinity.test.FlightAffinityTests;
//? if >=1.21.5 {
import net.baneina.flightaffinity.test.TestCompat;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
//?} else {
/*import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;

import java.util.Collection;
*///?}

// Registered through src/test/resources/fabric.mod.json (a separate "flightaffinity-test" mod).
//? if >=1.21.5 {
public class FabricGameTests implements ModInitializer {
    @Override
    public void onInitialize() {
        FlightAffinityTests.requireTestInstances();
        FlightAffinityTests.ALL.forEach((name, test) -> Registry.register(BuiltInRegistries.TEST_FUNCTION,
                TestCompat.id(FlightAffinityTests.NAMESPACE, name), test));
    }
}
//?} else {
/*public class FabricGameTests {
    @GameTestGenerator
    public Collection<TestFunction> tests() {
        return FlightAffinityTests.testFunctions();
    }
}
*///?}
