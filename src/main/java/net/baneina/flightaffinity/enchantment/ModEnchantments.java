package net.baneina.flightaffinity.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;
//? if >=1.21 {
import net.baneina.flightaffinity.FlightAffinity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
//?} else {
/*import java.util.function.Supplier;
*///?}

public final class ModEnchantments {
    private ModEnchantments() {}

    //? if >=1.21 {
    // Data-driven since 1.21: data/flightaffinity/enchantment/flight_affinity.json
    public static final ResourceKey<Enchantment> FLIGHT_AFFINITY =
            ResourceKey.create(Registries.ENCHANTMENT, FlightAffinity.id("flight_affinity"));
    //?} else {
    /*// Registered in code before 1.21 (see FlightAffinityEnchantment); each loader's entry point
    // points this at the registered instance.
    public static Supplier<? extends Enchantment> flightAffinity = () -> null;
    *///?}
}
