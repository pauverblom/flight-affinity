package net.baneina.flightaffinity.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.tags.ItemTags;

/**
 * The Flight Affinity enchantment.
 * When applied to a helmet, it removes the airborne mining-speed penalty.
 */
public class FlightAffinityEnchantment extends Enchantment {

    /** Default constructor. */
    public FlightAffinityEnchantment() {
        super(Enchantment.definition(
                ItemTags.HEAD_ARMOR_ENCHANTABLE,
                1,  // weight
                1,  // max level
                Enchantment.constantCost(1),
                Enchantment.constantCost(41),
                4,  // anvil cost
                EquipmentSlot.HEAD
        ));
    }
}

