package net.baneina.flightaffinity;

import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.fabricmc.api.ModInitializer;

public class FlightAffinity implements ModInitializer {

    public static final String MOD_ID = "flightaffinity";

    @Override
    public void onInitialize() {
        ModEnchantments.registerModEnchantments();
    }
}
