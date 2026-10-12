package net.baneina.flightaffinity.test.forge;

import net.baneina.flightaffinity.test.FlightAffinityTests;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraftforge.gametest.GameTestHolder;

import java.util.Collection;

@GameTestHolder(FlightAffinityTests.NAMESPACE)
public class ForgeGameTests {
    @GameTestGenerator
    public static Collection<TestFunction> tests() {
        return FlightAffinityTests.testFunctions();
    }
}
