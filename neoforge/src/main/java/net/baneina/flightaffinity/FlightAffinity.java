package net.baneina.flightaffinity;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/**
 * The NeoForge initialization entry point for the mod.
 */
@Mod("flightaffinity")
public class FlightAffinity {

    /**
     * Constructor for the FlightAffinity NeoForge mod.
     * @param eventBus The event bus to register events to.
     */
    public FlightAffinity(IEventBus eventBus) {
        FlightAffinityCommon.init();
    }
}