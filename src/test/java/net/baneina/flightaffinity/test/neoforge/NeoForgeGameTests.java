package net.baneina.flightaffinity.test.neoforge;

import net.baneina.flightaffinity.test.FlightAffinityTests;
//? if >=1.21.5 {
import net.baneina.flightaffinity.test.TestCompat;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
//?} else {
/*import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.neoforged.neoforge.gametest.GameTestHolder;

import java.util.Collection;
*///?}

//? if >=1.21.5 {
// Registered on the mod event bus by NeoForgeTestMod.
public class NeoForgeGameTests {
    static void register(RegisterEvent event) {
        if (!event.getRegistryKey().equals(BuiltInRegistries.TEST_FUNCTION.key())) return;
        FlightAffinityTests.ALL.forEach((name, test) -> event.register(BuiltInRegistries.TEST_FUNCTION.key(),
                TestCompat.id(FlightAffinityTests.NAMESPACE, name), () -> test));
    }
}
//?} else {
/*@GameTestHolder(FlightAffinityTests.NAMESPACE)
public class NeoForgeGameTests {
    @GameTestGenerator
    public static Collection<TestFunction> tests() {
        return FlightAffinityTests.testFunctions();
    }
}
*///?}
