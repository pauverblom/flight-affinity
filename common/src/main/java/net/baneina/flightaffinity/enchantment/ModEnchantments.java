package net.baneina.flightaffinity.enchantment;

import net.baneina.flightaffinity.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantments {

    public static final ResourceKey<Enchantment> FLIGHT_AFFINITY = ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "flight_affinity"));

    public static void registerModEnchantments() {
        System.out.println("Registering Enchantments for " + Constants.MOD_ID);
    }
}