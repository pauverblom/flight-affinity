package net.baneina.flightaffinity.test;

import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.Consumer;

@EventBusSubscriber(modid = "flightaffinity")
public class FlightAffinityGameTest {

    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key())) {

            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_bonus_applied_in_air"),
                    () -> (Consumer<GameTestHelper>) (GameTestHelper context) -> {
                        Player mockPlayer = context.makeMockPlayer(GameType.SURVIVAL);

                        ItemStack helmet = new ItemStack(Items.DIAMOND_HELMET);
                        var enchantmentEntry = context.getLevel().registryAccess()
                                .lookupOrThrow(Registries.ENCHANTMENT)
                                .getOrThrow(ModEnchantments.FLIGHT_AFFINITY);

                        helmet.enchant(enchantmentEntry, 1);
                        mockPlayer.setItemSlot(EquipmentSlot.HEAD, helmet);

                        mockPlayer.setOnGround(false);

                        float breakingSpeed = mockPlayer.getDestroySpeed(context.getLevel().getBlockState(context.absolutePos(new BlockPos(0, 0, 0))));

                        if (breakingSpeed < 1.0f) {
                            context.fail(Component.literal("Flight Affinity no preservó la velocidad minera en el aire!"));
                        }

                        context.succeed();
                    }
            );

            event.register(net.minecraft.core.registries.BuiltInRegistries.TEST_FUNCTION.key(),
                    Identifier.fromNamespaceAndPath("flightaffinity-test", "flight_affinity_spawns_properly"),
                    () -> (Consumer<GameTestHelper>) (GameTestHelper context) -> {
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
            );
        }
    }
}
