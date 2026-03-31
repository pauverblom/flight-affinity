package net.baneina.flightaffinity;

import net.fabricmc.api.ModInitializer;

/**
 * Fabric entry point for the Flight Affinity mod.
 */
public class FlightAffinity implements ModInitializer {
    
    /** Default constructor. */
    public FlightAffinity() {}

    @Override
    public void onInitialize() {
        FlightAffinityCommon.init();
    }
}
