package net.baneina.flightaffinity;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Shared constants. Loader entry points live in the fabric, neoforge and forge packages. */
public final class FlightAffinity {
    public static final String MOD_ID = "flightaffinity";
    public static final Logger LOG = LoggerFactory.getLogger("Flight Affinity");

    private FlightAffinity() {}

    public static Identifier id(String path) {
        return id(MOD_ID, path);
    }

    // Mojang renamed this class in 1.21.11; stonecutter.gradle.kts swaps in the old name for older targets.
    public static Identifier id(String namespace, String path) {
        //? if >=1.21 {
        return Identifier.fromNamespaceAndPath(namespace, path);
        //?} else {
        /*return new Identifier(namespace, path);
        *///?}
    }
}
