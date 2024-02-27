package net.baneina.flightaffinity;

import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class FlightAffinity implements ModInitializer {

    public static final String MOD_ID = "flightaffinity";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModEnchantments.registerModEnchantments();
    }
}
