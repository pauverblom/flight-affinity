package net.baneina.flightaffinity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common entry point for the Flight Affinity mod.
 */
public class FlightAffinityCommon {

    private static final Logger LOG = LoggerFactory.getLogger("FlightAffinity");

    /** Private constructor. */
    private FlightAffinityCommon() {
    }

    /**
     * Initializes the common module.
     * Enchantment registration must be completed by the platform entry point
     * <em>before</em> this method is called.
     */
    public static void init() {
        LOG.info("Hello from Flight Affinity init on Forge! we are currently in a {} environment!",
                net.minecraftforge.fml.loading.FMLLoader.isProduction() ? "production" : "development");

        if (net.minecraftforge.fml.ModList.get().isLoaded("flightaffinity")) {
            LOG.info("Hello from Flight Affinity");
        }
    }
}

