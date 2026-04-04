package net.baneina.flightaffinity;

import net.baneina.flightaffinity.enchantment.FlightAffinityEnchantment;
import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The NeoForge initialization entry point for the mod.
 */
@Mod("flightaffinity")
public class FlightAffinity {

    /**
     * DeferredRegister fires during RegisterEvent, before the enchantment registry
     * is frozen — allowing FlightAffinityEnchantment to call createIntrusiveHolder().
     */
    private static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(Registries.ENCHANTMENT, "flightaffinity");

    private static final DeferredHolder<Enchantment, FlightAffinityEnchantment> FLIGHT_AFFINITY_HOLDER =
            ENCHANTMENTS.register("flight_affinity", FlightAffinityEnchantment::new);

    /**
     * Constructor for the FlightAffinity NeoForge mod.
     * @param eventBus The event bus to register events to.
     */
    public FlightAffinity(IEventBus eventBus) {
        // Register with the event bus so RegisterEvent fires at the right time.
        ENCHANTMENTS.register(eventBus);
        // FMLCommonSetupEvent fires after RegisterEvent, so the holder is bound by then.
        eventBus.addListener(this::onCommonSetup);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        // Holder is now bound — wire it to the common reference used by the mixin/tests.
        ModEnchantments.FLIGHT_AFFINITY = FLIGHT_AFFINITY_HOLDER.get();
        FlightAffinityCommon.init();
    }
}