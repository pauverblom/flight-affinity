package net.baneina.flightaffinity;

import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Forge 1.20.1 entry point for the Flight Affinity mod.
 */
@Mod("flightaffinity")
public class FlightAffinity {

    private static final Logger LOG = LoggerFactory.getLogger("FlightAffinity");

    /**
     * Constructor for the FlightAffinity Forge mod.
     */
    public FlightAffinity() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register enchantments via DeferredRegister.
        ModEnchantments.register(modEventBus);

        // FMLCommonSetupEvent fires after registry events, so the holder is bound by then.
        modEventBus.addListener(this::onCommonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        // Wire the deferred-registered enchantment to the common static reference
        // used by the mixin and tests.
        ModEnchantments.bindDeferredInstance();
        FlightAffinityCommon.init();
    }
}

