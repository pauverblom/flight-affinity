package net.baneina.flightaffinity.enchantment;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Registry keys for mod enchantments.
 */
public class ModEnchantments {

    /** Private constructor to hide the implicit public one. */
    private ModEnchantments() {}

    /** The ResourceKey for the Flight Affinity enchantment. */
    public static final ResourceKey<Enchantment> FLIGHT_AFFINITY = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath("flightaffinity", "flight_affinity"));
}