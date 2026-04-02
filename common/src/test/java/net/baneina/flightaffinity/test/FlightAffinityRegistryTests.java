package net.baneina.flightaffinity.test;

import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EnchantmentTags;

public class FlightAffinityRegistryTests {

    public static void testFlightAffinitySpawnsProperly(GameTestHelper context) {
        var registry = context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        var enchantmentEntry = registry.getOrThrow(ModEnchantments.FLIGHT_AFFINITY);

        if (!enchantmentEntry.is(EnchantmentTags.TRADEABLE)) {
            context.fail(Component.literal("Flight Affinity is missing the TRADEABLE tag, villagers won't sell it!"));
            return;
        }

        if (!enchantmentEntry.is(EnchantmentTags.ON_RANDOM_LOOT)) {
            context.fail(Component.literal("Flight Affinity is missing the ON_RANDOM_LOOT tag, it won't appear in chests!"));
            return;
        }

        if (!enchantmentEntry.is(EnchantmentTags.IN_ENCHANTING_TABLE)) {
            context.fail(Component.literal("Flight Affinity is missing the IN_ENCHANTING_TABLE tag!"));
            return;
        }

        context.succeed();
    }
}

