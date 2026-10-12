package net.baneina.flightaffinity.test.forge;

import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.gametest.framework.GameTestTicker;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.gametest.ForgeGameTestHooks;

/*
 * The GameTests are a separate mod (tests are found through @GameTestHolder on ForgeGameTests).
 *
 * Forge only registers @GameTestHolder classes and ticks GameTests in development
 * (ForgeGameTestHooks.isGametestEnabled() is false in production). On a production GameTest server
 * (gradle/production.gradle) the tests do both themselves.
 */
@Mod("flightaffinity_test")
public class ForgeTestMod {
    public ForgeTestMod() {
        if (!ForgeGameTestHooks.isGametestEnabled() && Boolean.getBoolean("forge.gameTestServer"))
            GameTestRegistry.register(ForgeGameTests.class);
        MinecraftForge.EVENT_BUS.addListener(ForgeTestMod::tickGameTests);
    }

    private static void tickGameTests(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.getServer() instanceof GameTestServer && !ForgeGameTestHooks.isGametestEnabled())
            GameTestTicker.SINGLETON.tick();
    }
}
