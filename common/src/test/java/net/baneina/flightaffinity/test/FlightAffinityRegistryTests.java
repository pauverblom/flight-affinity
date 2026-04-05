package net.baneina.flightaffinity.test;

import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;

public class FlightAffinityRegistryTests {

    public static void testFlightAffinitySpawnsProperly(GameTestHelper context) {
        // In 1.20.4, enchantments are code-registered objects. Verify the enchantment
        // is present in the registry.
        var key = BuiltInRegistries.ENCHANTMENT.getKey(ModEnchantments.FLIGHT_AFFINITY);
        if (key == null) {
            context.fail("Flight Affinity enchantment is not registered!");
            return;
        }

        var expected = new ResourceLocation("flightaffinity", "flight_affinity");
        if (!expected.equals(key)) {
            context.fail("Flight Affinity registered under wrong key: " + key);
            return;
        }

        context.succeed();
    }
}
