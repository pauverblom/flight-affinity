package net.baneina.flightaffinity.test.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
//? if <1.21.5 {
/*import net.minecraft.gametest.framework.GameTestRegistry;
import net.neoforged.fml.loading.FMLLoader;
*///?}

// The GameTests are a separate mod so that smoke tests can run them against a release jar.
@Mod("flightaffinity_test")
public class NeoForgeTestMod {
    public NeoForgeTestMod(IEventBus modBus) {
        //? if >=1.21.5 {
        modBus.addListener(NeoForgeGameTests::register);
        //?} else {
        /*// NeoForge only registers @GameTestHolder classes in development. A production GameTest server
        // (gradle/production-smoke.gradle) collects the tests after mods are constructed, so add them here.
        if (FMLLoader.isProduction() && Boolean.getBoolean("neoforge.gameTestServer"))
            GameTestRegistry.register(NeoForgeGameTests.class);
        *///?}
    }
}
