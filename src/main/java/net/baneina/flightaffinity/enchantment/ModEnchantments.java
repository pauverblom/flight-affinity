package net.baneina.flightaffinity.enchantment;

import net.baneina.flightaffinity.FlightAffinity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEnchantments {

    public static final RegistryKey<Enchantment> FLIGHT_AFFINITY = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(FlightAffinity.MOD_ID, "flight_affinity"));

    public static void registerModEnchantments() {
        System.out.println("Registering Enchantments for " + FlightAffinity.MOD_ID);
    }
}
