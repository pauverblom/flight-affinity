package net.baneina.flightaffinity;

import net.baneina.flightaffinity.platform.Services;

/**
 * Common entry point for the Flight Affinity mod.
 */
public class FlightAffinityCommon {

    /** Private constructor. */
    private FlightAffinityCommon() {
    }

    /**
     * Initializes the common module.
     */
    public static void init() {

        Constants.LOG.info("Hello from Flight Affinity init on {}! we are currently in a {} environment!", Services.PLATFORM.getPlatformName(), Services.PLATFORM.getEnvironmentName());

        if (Services.PLATFORM.isModLoaded("flightaffinity")) {

            Constants.LOG.info("Hello from Flight Affinity");
        }
    }
}