package net.baneina.flightaffinity.test;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

/**
 * Only compiled for targets before 1.20.5 (see gradle/common.gradle), where a mock player added
 * to the world loses its bounding box. Instead of placing water, its eyes are marked as submerged.
 */
final class LegacyFluids {
    private LegacyFluids() {}

    @SuppressWarnings("unchecked")
    static void submergeEyes(GameTestHelper helper, Player player) {
        try {
            // Entity.fluidOnEyes. Outside development its name is remapped and reflection by name would not
            // follow, so try each Set field and keep the one that makes isEyeInFluid(WATER) true.
            boolean marked = false;
            for (Field field : Entity.class.getDeclaredFields()) {
                if (field.getType() != Set.class || Modifier.isStatic(field.getModifiers())) continue;
                field.setAccessible(true);
                Set<Object> set = (Set<Object>) field.get(player);
                try {
                    if (set == null || !set.add(FluidTags.WATER)) continue;
                } catch (UnsupportedOperationException immutable) {
                    continue;
                }
                if (player.isEyeInFluid(FluidTags.WATER)) {
                    marked = true;
                    break;
                }
                set.remove(FluidTags.WATER);
            }
            if (!marked) throw new NoSuchFieldException("Entity.fluidOnEyes");

            // Forge and NeoForge check their own fluid-type field instead (added by them, so never remapped).
            //? if forge {
            /*Field forge = Entity.class.getDeclaredField("forgeFluidTypeOnEyes");
            forge.setAccessible(true);
            forge.set(player, net.minecraftforge.common.ForgeMod.WATER_TYPE.get());
            *///?} else if neoforge {
            /*Field forge = Entity.class.getDeclaredField("forgeFluidTypeOnEyes");
            forge.setAccessible(true);
            forge.set(player, net.neoforged.neoforge.common.NeoForgeMod.WATER_TYPE.value());
            *///?}
        } catch (ReflectiveOperationException e) {
            TestCompat.fail(helper, "could not mark the player's eyes as submerged: " + e);
        }
    }
}
