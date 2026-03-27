package net.baneina.flightaffinity.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.tag.ItemTags;

public class FlightAffinityEnchantment extends Enchantment {
    public FlightAffinityEnchantment() {
        super(Enchantment.properties(
            ItemTags.HEAD_ARMOR_ENCHANTABLE,
            1, // weight
            1, // max level
            Enchantment.constantCost(1),
            Enchantment.constantCost(41),
            4, // anvil cost
            EquipmentSlot.HEAD
        ));
    }
}
