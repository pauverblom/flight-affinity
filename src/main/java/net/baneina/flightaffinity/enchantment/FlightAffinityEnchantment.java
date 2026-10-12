package net.baneina.flightaffinity.enchantment;

// Only compiled for targets before 1.21 (see gradle/common.gradle). From 1.21 on the enchantment
// is defined by data/flightaffinity/enchantment/flight_affinity.json; keep the numbers in sync:
// weight 2, max level 1, enchanting cost 15-45, anvil cost 4, helmets only.

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
//? if >=1.20.5 {
import net.minecraft.tags.ItemTags;
//?} else {
/*import net.minecraft.world.item.enchantment.EnchantmentCategory;
*///?}

public class FlightAffinityEnchantment extends Enchantment {
    private static final int MIN_COST = 15;
    private static final int MAX_COST = 45;

    public FlightAffinityEnchantment() {
        //? if >=1.20.5 {
        super(Enchantment.definition(ItemTags.HEAD_ARMOR_ENCHANTABLE, 2, 1,
                Enchantment.constantCost(MIN_COST), Enchantment.constantCost(MAX_COST), 4, EquipmentSlot.HEAD));
        //?} else {
        /*super(Rarity.RARE, EnchantmentCategory.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD});
        *///?}
    }

    //? if <1.20.5 {
    /*@Override
    public int getMinCost(int level) {
        return MIN_COST;
    }

    @Override
    public int getMaxCost(int level) {
        return MAX_COST;
    }
    *///?}
}
