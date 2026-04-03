package net.baneina.flightaffinity.platform;

import net.baneina.flightaffinity.platform.services.IPlatformHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

/**
 * Platform helper implementation for NeoForge.
 */
public class NeoForgePlatformHelper implements IPlatformHelper {

    /** Default constructor. */
    public NeoForgePlatformHelper() {}

    @Override
    public String getPlatformName() {

        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }
}