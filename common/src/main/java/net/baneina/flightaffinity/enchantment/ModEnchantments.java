package net.baneina.flightaffinity.enchantment;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mod enchantment registry.
 */
public class ModEnchantments {

    private static final Logger LOG = LoggerFactory.getLogger("FlightAffinity");

    /** Private constructor to hide the implicit public one. */
    private ModEnchantments() {}

    /**
     * The Flight Affinity enchantment instance.
     * Set by platform-specific code before any mixin or test can reference it.
     * <ul>
     *   <li>Fabric: assigned inside {@link #register()} called during mod init.</li>
     *   <li>NeoForge: assigned by the {@code @Mod} class after {@code DeferredRegister}
     *       fires the {@code RegisterEvent}.</li>
     * </ul>
     */
    public static Enchantment FLIGHT_AFFINITY;

    /**
     * Registers all mod enchantments.  Must be called during mod initialization
     * <em>before</em> the enchantment registry is frozen.
     * On Fabric this is called from the mod's {@code onInitialize()} entry point.
     * On NeoForge, use {@code DeferredRegister} instead of calling this method.
     */
    public static void register() {
        FLIGHT_AFFINITY = Registry.register(BuiltInRegistries.ENCHANTMENT,
                new ResourceLocation("flightaffinity", "flight_affinity"),
                new FlightAffinityEnchantment());
        LOG.info("Registered enchantments for flightaffinity");
    }
}