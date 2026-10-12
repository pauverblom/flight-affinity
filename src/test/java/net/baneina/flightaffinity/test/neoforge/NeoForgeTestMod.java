package net.baneina.flightaffinity.test.neoforge;

import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.gametest.framework.GameTestTicker;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.gametest.GameTestHooks;
//? if <1.21.5 {
/*import net.minecraft.gametest.framework.GameTestRegistry;
*///?}
//? if >=1.20.5 {
import net.neoforged.neoforge.event.tick.ServerTickEvent;
//?} else {
/*import net.neoforged.neoforge.event.TickEvent;
*///?}

/*
 * The GameTests are a separate mod so that they can run against a release jar.
 *
 * NeoForge only registers @GameTestHolder classes and ticks GameTests in development
 * (GameTestHooks.isGametestEnabled() is false in production). On a production GameTest server
 * (gradle/production.gradle) the tests do both themselves.
 */
@Mod("flightaffinity_test")
public class NeoForgeTestMod {
    public NeoForgeTestMod(IEventBus modBus) {
        //? if >=1.21.5 {
        modBus.addListener(NeoForgeGameTests::register);
        //?} else {
        /*if (!GameTestHooks.isGametestEnabled() && Boolean.getBoolean("neoforge.gameTestServer"))
            GameTestRegistry.register(NeoForgeGameTests.class);
        *///?}
        NeoForge.EVENT_BUS.addListener(NeoForgeTestMod::tickGameTests);
    }

    //? if >=1.20.5 {
    private static void tickGameTests(ServerTickEvent.Post event) {
        tick(event.getServer());
    }
    //?} else {
    /*private static void tickGameTests(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) tick(event.getServer());
    }
    *///?}

    private static void tick(MinecraftServer server) {
        if (server instanceof GameTestServer && !GameTestHooks.isGametestEnabled()) GameTestTicker.SINGLETON.tick();
    }
}
