package net.baneina.flightaffinity.forge;

import net.baneina.flightaffinity.FlightAffinity;
import net.baneina.flightaffinity.enchantment.FlightAffinityEnchantment;
import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// Forge is only built for 1.20.1, so this file has no version conditions.
@Mod(FlightAffinity.MOD_ID)
public class FlightAffinityForge {
    private static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, FlightAffinity.MOD_ID);
    private static final RegistryObject<FlightAffinityEnchantment> FLIGHT_AFFINITY =
            ENCHANTMENTS.register("flight_affinity", FlightAffinityEnchantment::new);

    public FlightAffinityForge() {
        ENCHANTMENTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModEnchantments.flightAffinity = FLIGHT_AFFINITY;
    }
}
