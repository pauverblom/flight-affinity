package net.baneina.flightaffinity;

import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.fabricmc.api.ModInitializer;

/**
 * Fabric entry point for the Flight Affinity mod.
 */
public class FlightAffinity implements ModInitializer {
    
    /** Default constructor. */
    public FlightAffinity() {}

    @Override
    public void onInitialize() {
        // Register enchantments before the registry is frozen (Fabric mod init fires at the right time).
        ModEnchantments.register();
        FlightAffinityCommon.init();
    }
}
