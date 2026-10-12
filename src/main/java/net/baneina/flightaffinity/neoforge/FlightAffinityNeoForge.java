package net.baneina.flightaffinity.neoforge;

import net.baneina.flightaffinity.FlightAffinity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
//? if <1.21 {
/*import net.baneina.flightaffinity.enchantment.FlightAffinityEnchantment;
import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
*///?}

@Mod(FlightAffinity.MOD_ID)
public class FlightAffinityNeoForge {
    //? if <1.21 {
    /*private static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(Registries.ENCHANTMENT, FlightAffinity.MOD_ID);
    private static final DeferredHolder<Enchantment, FlightAffinityEnchantment> FLIGHT_AFFINITY =
            ENCHANTMENTS.register("flight_affinity", FlightAffinityEnchantment::new);
    *///?}

    public FlightAffinityNeoForge(IEventBus modBus) {
        //? if <1.21 {
        /*ENCHANTMENTS.register(modBus);
        ModEnchantments.flightAffinity = FLIGHT_AFFINITY;
        *///?}
        FlightAffinity.LOG.debug("Flight Affinity initialized");
    }
}
