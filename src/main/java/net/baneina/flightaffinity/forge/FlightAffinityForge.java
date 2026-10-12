package net.baneina.flightaffinity.forge;

import net.baneina.flightaffinity.FlightAffinity;
import net.baneina.flightaffinity.enchantment.FlightAffinityEnchantment;
import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
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
        MinecraftForge.EVENT_BUS.addListener(FlightAffinityForge::ignoreAirbornePenalty);
    }

    /*
     * Forge's Player.getDigSpeed divides the speed by 5 when the player is not on the ground, then
     * posts BreakSpeed with the result; this undoes the division. Forge uses this event instead of the
     * mixin of the other loaders: Forge 1.20.1 has no MixinExtras, and a plain @Redirect of the same
     * call would make the game refuse to start next to any other mod redirecting it.
     */
    private static void ignoreAirbornePenalty(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (!player.onGround() && EnchantmentHelper.getEnchantmentLevel(FLIGHT_AFFINITY.get(), player) > 0) {
            event.setNewSpeed(event.getNewSpeed() * 5.0F);
        }
    }
}
