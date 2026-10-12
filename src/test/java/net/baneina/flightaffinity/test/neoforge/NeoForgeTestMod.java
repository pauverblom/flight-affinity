package net.baneina.flightaffinity.test.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

// The GameTests are a separate mod so that smoke tests can run them against a release jar.
@Mod("flightaffinity_test")
public class NeoForgeTestMod {
    public NeoForgeTestMod(IEventBus modBus) {
        //? if >=1.21.5 {
        modBus.addListener(NeoForgeGameTests::register);
        //?}
    }
}
