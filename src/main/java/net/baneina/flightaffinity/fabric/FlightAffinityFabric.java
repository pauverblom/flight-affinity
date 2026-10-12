package net.baneina.flightaffinity.fabric;

import net.baneina.flightaffinity.FlightAffinity;
import net.fabricmc.api.ModInitializer;
//? if <1.21 {
/*import net.baneina.flightaffinity.enchantment.FlightAffinityEnchantment;
import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
*///?}

public class FlightAffinityFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        //? if <1.21 {
        /*FlightAffinityEnchantment enchantment = Registry.register(BuiltInRegistries.ENCHANTMENT,
                FlightAffinity.id("flight_affinity"), new FlightAffinityEnchantment());
        ModEnchantments.flightAffinity = () -> enchantment;
        *///?}
        FlightAffinity.LOG.debug("Flight Affinity initialized");
    }
}
