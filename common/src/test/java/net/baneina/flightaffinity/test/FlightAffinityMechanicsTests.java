package net.baneina.flightaffinity.test;

import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static net.baneina.flightaffinity.test.FlightAffinityTestConstants.*;

public class FlightAffinityMechanicsTests {

    public static void testFlightAffinityAirAirborneUnequipAffinity(GameTestHelper context) {
        Player mockPlayer = context.makeMockPlayer();

        // Initialise position (sets AABB).
        BlockPos spawnPos = context.absolutePos(new BlockPos(0, 0, 0));
        mockPlayer.setPos(spawnPos.getX() + 0.5, spawnPos.getY() + 0.2, spawnPos.getZ() + 0.5);

        ItemStack helmet = new ItemStack(HELMET_TYPE);
        helmet.enchant(ModEnchantments.FLIGHT_AFFINITY, 1);
        mockPlayer.setItemSlot(EquipmentSlot.HEAD, helmet);
        mockPlayer.setOnGround(false);

        BlockPos targetPos = new BlockPos(0, 4, 0);
        context.setBlock(targetPos, DEFAULT_TARGET_BLOCK);

        // Measure with enchantment equipped.
        float before = mockPlayer.getDestroySpeed(context.getLevel().getBlockState(context.absolutePos(targetPos)));
        if (before < NO_PENALTY_MIN || before > NO_PENALTY_MAX) {
            context.fail("air_airborne_unequip_affinity expected initial speed in [0.99, 1.01] but got " + before);
            return;
        }

        // Unequip helmet.
        mockPlayer.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        mockPlayer.setOnGround(false);

        float after = mockPlayer.getDestroySpeed(context.getLevel().getBlockState(context.absolutePos(targetPos)));
        if (after < SINGLE_PENALTY_MIN || after > SINGLE_PENALTY_MAX) {
            context.fail("air_airborne_unequip_affinity expected post-unequip speed in [0.19, 0.21] but got " + after);
            return;
        }
        context.succeed();
    }
}
