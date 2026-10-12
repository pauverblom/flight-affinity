package net.baneina.flightaffinity.test;

import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
//? if >=1.20.5 {
import net.minecraft.world.level.GameType;
//?}
//? if <1.21 {
/*import net.baneina.flightaffinity.FlightAffinity;
*///?}
//? if >=1.21 {
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;
//?}
//? if >=1.21.2 {
import net.minecraft.world.entity.EntitySpawnReason;
//?}
//? if >=1.21.5 {
import net.minecraft.network.chat.Component;
//?}

/** The Minecraft APIs the tests use that changed between versions, in one place. */
public final class TestCompat {
    private TestCompat() {}

    // The tests are compiled for each Minecraft version but run with a jar built for another one, so
    // they only call the mod for what they test. FlightAffinity.id would tie them to the id class of
    // the version the jar was built for, which Mojang renamed in 1.21.11.
    public static Identifier id(String namespace, String path) {
        //? if >=1.21 {
        return Identifier.fromNamespaceAndPath(namespace, path);
        //?} else {
        /*return new Identifier(namespace, path);
        *///?}
    }

    static void fail(GameTestHelper helper, String message) {
        //? if >=1.21.5 {
        helper.fail(Component.literal(message));
        //?} else {
        /*helper.fail(message);
        *///?}
    }

    static Player mockPlayer(GameTestHelper helper) {
        //? if >=1.20.5 {
        return helper.makeMockPlayer(GameType.SURVIVAL);
        //?} else {
        /*return helper.makeMockPlayer();
        *///?}
    }

    // Looked up by id: 26.2 removed constants such as EntityType.MINECART.
    static EntityType<?> entityType(String path) {
        //? if >=1.21.2 {
        return BuiltInRegistries.ENTITY_TYPE.getValue(id("minecraft", path));
        //?} else {
        /*return BuiltInRegistries.ENTITY_TYPE.get(id("minecraft", path));
        *///?}
    }

    static <T extends Entity> T create(GameTestHelper helper, EntityType<T> type) {
        //? if >=1.21.2 {
        return type.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        //?} else {
        /*return type.create(helper.getLevel());
        *///?}
    }

    static void enchant(Player player, ItemStack stack, boolean flightAffinity, boolean aquaAffinity) {
        //? if >=1.21 {
        if (flightAffinity) stack.enchant(enchantment(player, ModEnchantments.FLIGHT_AFFINITY), 1);
        if (aquaAffinity) stack.enchant(enchantment(player, Enchantments.AQUA_AFFINITY), 1);
        //?} else {
        /*if (flightAffinity) stack.enchant(ModEnchantments.flightAffinity.get(), 1);
        if (aquaAffinity) stack.enchant(Enchantments.AQUA_AFFINITY, 1);
        *///?}
    }

    //? if >=1.21 {
    private static Holder<Enchantment> enchantment(Player player, ResourceKey<Enchantment> key) {
        //? if >=1.21.2 {
        return player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
        //?} else {
        /*return player.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(key);
        *///?}
    }
    //?}

    /** The enchantment exists and can be found in the enchanting table, on loot and from villagers. */
    static void enchantmentIsObtainable(GameTestHelper helper) {
        //? if >=1.21 {
        Player player = mockPlayer(helper);
        Holder<Enchantment> holder = enchantment(player, ModEnchantments.FLIGHT_AFFINITY);
        if (!holder.is(EnchantmentTags.IN_ENCHANTING_TABLE)) { fail(helper, "missing from #in_enchanting_table"); return; }
        if (!holder.is(EnchantmentTags.ON_RANDOM_LOOT)) { fail(helper, "missing from #on_random_loot"); return; }
        if (!holder.is(EnchantmentTags.TRADEABLE)) { fail(helper, "missing from #tradeable"); return; }
        //?} else {
        /*var enchantment = ModEnchantments.flightAffinity.get();
        var key = enchantment == null ? null : BuiltInRegistries.ENCHANTMENT.getKey(enchantment);
        if (!id(FlightAffinity.MOD_ID, "flight_affinity").equals(key)) { fail(helper, "registered as " + key); return; }
        if (!enchantment.isDiscoverable() || !enchantment.isTradeable() || enchantment.isTreasureOnly()) {
            fail(helper, "not obtainable from the enchanting table, loot and villagers");
            return;
        }
        *///?}
        helper.succeed();
    }
}
