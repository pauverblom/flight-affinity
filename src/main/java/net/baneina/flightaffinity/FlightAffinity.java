package net.baneina.flightaffinity;

import net.baneina.flightaffinity.enchantment.FlightAffinityEnchantment;
import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.fabricmc.api.ModInitializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.collection.Weight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlightAffinity implements ModInitializer {

    public static final String MOD_ID = "flightaffinity";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModEnchantments.registerModEnchantments();
    }
}
