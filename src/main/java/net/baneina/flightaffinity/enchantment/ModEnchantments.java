package net.baneina.flightaffinity.enchantment;

import net.baneina.flightaffinity.FlightAffinity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public class ModEnchantments {

    public static Enchantment FLIGHT_AFFINITY = register("flight_affinity",
            new FlightAffinityEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.HEAD));


    private static Enchantment register(String name, Enchantment enchantment) {
        return Registry.register(Registries.ENCHANTMENT, new Identifier(FlightAffinity.MOD_ID, name), enchantment);
    }
    public static void registerModEnchantments() {
        System.out.println("Registering Enchantments for " + FlightAffinity.MOD_ID);
    }
}
