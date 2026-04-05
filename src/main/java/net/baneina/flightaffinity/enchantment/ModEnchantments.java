package net.baneina.flightaffinity.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mod enchantment registry.
 */
public class ModEnchantments {

    private static final Logger LOG = LoggerFactory.getLogger("FlightAffinity");

    /** Private constructor to hide the implicit public one. */
    private ModEnchantments() {}

    private static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, "flightaffinity");

    private static final RegistryObject<FlightAffinityEnchantment> FLIGHT_AFFINITY_HOLDER =
            ENCHANTMENTS.register("flight_affinity", FlightAffinityEnchantment::new);

    /**
     * The Flight Affinity enchantment instance.
     * Set by {@link #bindDeferredInstance()} after registration completes.
     * Accessed by the mixin and tests.
     */
    public static Enchantment FLIGHT_AFFINITY;

    /**
     * Registers the DeferredRegister on the mod event bus.
     * Must be called during mod construction.
     *
     * @param eventBus The mod event bus.
     */
    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
        LOG.info("Registered enchantments for flightaffinity");
    }

    /**
     * Wires the deferred holder to the static field after registration fires.
     * Must be called from {@code FMLCommonSetupEvent} or later.
     */
    public static void bindDeferredInstance() {
        FLIGHT_AFFINITY = FLIGHT_AFFINITY_HOLDER.get();
    }
}

