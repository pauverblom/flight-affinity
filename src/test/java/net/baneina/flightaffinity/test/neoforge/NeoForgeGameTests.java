package net.baneina.flightaffinity.test.neoforge;

import net.baneina.flightaffinity.test.FlightAffinityTests;
//? if >=1.21.5 {
import net.baneina.flightaffinity.FlightAffinity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;
//?} else {
/*import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.neoforged.neoforge.gametest.GameTestHolder;

import java.util.Collection;
*///?}

//? if >=1.21.5 {
@EventBusSubscriber(modid = FlightAffinity.MOD_ID)
public class NeoForgeGameTests {
    @SubscribeEvent
    public static void register(RegisterEvent event) {
        if (!event.getRegistryKey().equals(BuiltInRegistries.TEST_FUNCTION.key())) return;
        FlightAffinityTests.ALL.forEach((name, test) -> event.register(BuiltInRegistries.TEST_FUNCTION.key(),
                FlightAffinity.id(FlightAffinityTests.NAMESPACE, name), () -> test));
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
