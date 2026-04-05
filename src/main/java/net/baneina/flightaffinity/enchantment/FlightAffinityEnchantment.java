package net.baneina.flightaffinity.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * The Flight Affinity enchantment.
 * When applied to a helmet, it removes the airborne mining-speed penalty.
 */
public class FlightAffinityEnchantment extends Enchantment {

    /** Default constructor. */
    public FlightAffinityEnchantment() {
        super(Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD});
    }

    @Override
    public int getMinCost(int level) {
        return 1;
    }

    @Override
    public int getMaxCost(int level) {
        return 41;
    }
}

