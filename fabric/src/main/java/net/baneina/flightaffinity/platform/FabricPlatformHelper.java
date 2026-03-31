package net.baneina.flightaffinity.platform;

import net.baneina.flightaffinity.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Platform helper implementation for Fabric.
 */
public class FabricPlatformHelper implements IPlatformHelper {

    /** Default constructor. */
    public FabricPlatformHelper() {}

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
