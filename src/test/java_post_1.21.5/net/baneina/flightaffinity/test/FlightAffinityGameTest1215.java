package net.baneina.flightaffinity.test;

import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

import java.util.function.Consumer;

public class FlightAffinityGameTest1215 implements ModInitializer {

    @Override
    public void onInitialize() {
        Registry.register(
            Registries.TEST_FUNCTION,
            Identifier.of("flightaffinity-test", "flight_affinity_bonus_applied_in_air"),
            (Consumer<TestContext>) (TestContext context) -> {
                PlayerEntity mockPlayer = context.createMockPlayer(GameMode.SURVIVAL);

                ItemStack helmet = new ItemStack(Items.DIAMOND_HELMET);
                var enchantmentEntry = context.getWorld().getRegistryManager()
                        .getOrThrow(RegistryKeys.ENCHANTMENT)
                        .getOrThrow(ModEnchantments.FLIGHT_AFFINITY);

                helmet.addEnchantment(enchantmentEntry, 1);
                mockPlayer.equipStack(net.minecraft.entity.EquipmentSlot.HEAD, helmet);

                mockPlayer.setOnGround(false);
                context.getWorld().spawnEntity(mockPlayer);

                float breakingSpeed = mockPlayer.getBlockBreakingSpeed(context.getWorld().getBlockState(context.getAbsolutePos(new BlockPos(0, 0, 0))));

                if (breakingSpeed < 1.0f) {
                    context.throwPositionedException(Text.literal("Flight Affinity no preservó la velocidad minera en el aire!"), mockPlayer);
                }

                context.complete();
            }
        );

        Registry.register(
            Registries.TEST_FUNCTION,
            Identifier.of("flightaffinity-test", "flight_affinity_spawns_properly"),
            (Consumer<TestContext>) (TestContext context) -> {
                var registry = context.getWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
                var enchantmentEntry = registry.getOrThrow(ModEnchantments.FLIGHT_AFFINITY);

                if (!enchantmentEntry.isIn(EnchantmentTags.TRADEABLE)) {
                    context.throwGameTestException(Text.of("Flight Affinity is missing the TRADEABLE tag, villagers won't sell it!"));
                }

                if (!enchantmentEntry.isIn(EnchantmentTags.ON_RANDOM_LOOT)) {
                    context.throwGameTestException(Text.of("Flight Affinity is missing the ON_RANDOM_LOOT tag, it won't appear in chests!"));
                }

                if (!enchantmentEntry.isIn(EnchantmentTags.IN_ENCHANTING_TABLE)) {
                    context.throwGameTestException(Text.of("Flight Affinity is missing the IN_ENCHANTING_TABLE tag!"));
                }

                context.complete();
            }
        );
    }
}

