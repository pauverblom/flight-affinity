package net.baneina.flightaffinity;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/**
 * NeoForge entry point for the Flight Affinity mod.
 */
@Mod(Constants.MOD_ID)
public class FlightAffinity {

    /**
     * Constructor for the FlightAffinity NeoForge mod.
     * @param eventBus The event bus to register events to.
     */
    public FlightAffinity(IEventBus eventBus) {
        FlightAffinityCommon.init();
    }
}