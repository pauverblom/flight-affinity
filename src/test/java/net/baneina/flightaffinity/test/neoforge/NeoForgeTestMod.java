package net.baneina.flightaffinity.test.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
//? if <1.21.5 {
/*import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.GameTestTicker;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
*///?}
//? if >=1.20.5 <1.21.5 {
/*import net.neoforged.neoforge.event.tick.ServerTickEvent;
*///?} else if <1.20.5 {
/*import net.neoforged.neoforge.event.TickEvent;
*///?}

// The GameTests are a separate mod so that smoke tests can run them against a release jar.
@Mod("flightaffinity_test")
public class NeoForgeTestMod {
    public NeoForgeTestMod(IEventBus modBus) {
        //? if >=1.21.5 {
        modBus.addListener(NeoForgeGameTests::register);
        //?} else {
        /*// NeoForge only registers @GameTestHolder classes and ticks GameTests in development. On a
        // production GameTest server (gradle/production-smoke.gradle) the tests do both themselves.
        if (FMLLoader.isProduction() && Boolean.getBoolean("neoforge.gameTestServer")) {
            GameTestRegistry.register(NeoForgeGameTests.class);
            NeoForge.EVENT_BUS.addListener(NeoForgeTestMod::tickGameTests);
        }
        *///?}
    }

    //? if >=1.20.5 <1.21.5 {
    /*private static void tickGameTests(ServerTickEvent.Post event) {
        GameTestTicker.SINGLETON.tick();
    }
    *///?} else if <1.20.5 {
    /*private static void tickGameTests(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) GameTestTicker.SINGLETON.tick();
    }
    *///?}
}
