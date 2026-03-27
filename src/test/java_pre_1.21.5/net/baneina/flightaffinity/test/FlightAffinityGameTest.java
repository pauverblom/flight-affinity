package net.baneina.flightaffinity.test;

import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

public class FlightAffinityGameTest {

    @GameTest(templateName = net.fabricmc.fabric.api.gametest.v1.FabricGameTest.EMPTY_STRUCTURE)
    public void testFlightAffinityBonusAppliedInAir(TestContext context) {
        // 1. Crear a un jugador artificial para la prueba
        PlayerEntity mockPlayer = context.createMockPlayer(GameMode.SURVIVAL);

        // 2. Darle el casco con Flight Affinity
        ItemStack helmet = new ItemStack(Items.DIAMOND_HELMET);
        var enchantmentEntry = context.getWorld().getRegistryManager()
                .getOrThrow(RegistryKeys.ENCHANTMENT)
                .getOrThrow(ModEnchantments.FLIGHT_AFFINITY);

        helmet.addEnchantment(enchantmentEntry, 1);
        mockPlayer.equipStack(net.minecraft.entity.EquipmentSlot.HEAD, helmet);

        // 3. Simular que NO está en el suelo (en el aire)
        mockPlayer.setOnGround(false);
        context.getWorld().spawnEntity(mockPlayer);

        // 4. Testear lo que afecta nuestro Mixin (getBlockBreakingSpeed)
        // Simulamos que quiere romper piedra
        float breakingSpeed = mockPlayer.getBlockBreakingSpeed(context.getWorld().getBlockState(context.getAbsolutePos(new BlockPos(0, 0, 0))));

        // Como Flight Affinity simula "estar en el suelo", la velocidad no debería dividirse por 5 al estar en el aire
        if (breakingSpeed < 1.0f) { // Puedes ajustar esta aserción matemática basándote en la velocidad por defecto del jugador
            context.throwPositionedException("Flight Affinity no preservó la velocidad minera en el aire!", mockPlayer);
        }

        context.complete();
    }

    @GameTest(templateName = net.fabricmc.fabric.api.gametest.v1.FabricGameTest.EMPTY_STRUCTURE)
    public void testFlightAffinitySpawnsProperly(TestContext context) {
        var registry = context.getWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        var enchantmentEntry = registry.getOrThrow(ModEnchantments.FLIGHT_AFFINITY);

        if (!enchantmentEntry.isIn(EnchantmentTags.TRADEABLE)) {
            context.throwGameTestException("Flight Affinity is missing the TRADEABLE tag, villagers won't sell it!");
        }

        if (!enchantmentEntry.isIn(EnchantmentTags.ON_RANDOM_LOOT)) {
            context.throwGameTestException("Flight Affinity is missing the ON_RANDOM_LOOT tag, it won't appear in chests!");
        }

        if (!enchantmentEntry.isIn(EnchantmentTags.IN_ENCHANTING_TABLE)) {
            context.throwGameTestException("Flight Affinity is missing the IN_ENCHANTING_TABLE tag!");
        }

        context.complete();
    }
}
