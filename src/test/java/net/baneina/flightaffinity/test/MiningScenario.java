package net.baneina.flightaffinity.test;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

/**
 * Puts a mock player in a given situation and checks the mining speed against stone.
 * Vanilla applies two independent x0.2 penalties: being airborne (removed by Flight Affinity)
 * and having the head under water (removed by Aqua Affinity).
 */
public final class MiningScenario {
    public enum Medium { AIR, WATER, LAVA }

    private static final BlockPos TARGET = new BlockPos(0, 4, 0);
    private static final int SETTLE_TICKS = 2;
    private static final float PENALTY = 0.2F;
    private static final float TOLERANCE = 0.05F;

    private final GameTestHelper helper;
    private final String name;
    private Medium medium = Medium.AIR;
    private boolean airborne;
    private boolean aquaAffinity;
    private boolean flightAffinity;
    private EntityType<?> vehicle;

    public MiningScenario(GameTestHelper helper, String name) {
        this.helper = helper;
        this.name = name;
    }

    public MiningScenario in(Medium medium) { this.medium = medium; return this; }
    public MiningScenario airborne(boolean airborne) { this.airborne = airborne; return this; }
    public MiningScenario aquaAffinity(boolean on) { this.aquaAffinity = on; return this; }
    public MiningScenario flightAffinity(boolean on) { this.flightAffinity = on; return this; }
    public MiningScenario riding(EntityType<?> vehicle) { this.vehicle = vehicle; this.airborne = true; return this; }

    private float expectedSpeed() {
        float speed = 1.0F;
        if (medium == Medium.WATER && !aquaAffinity) speed *= PENALTY;
        if (airborne && !flightAffinity) speed *= PENALTY;
        return speed;
    }

    public void run() {
        Player player = TestCompat.mockPlayer(helper);
        BlockPos spawn = helper.absolutePos(BlockPos.ZERO);
        player.setPos(spawn.getX() + 0.5, spawn.getY() + 0.2, spawn.getZ() + 0.5);
        equipHelmet(player, aquaAffinity, flightAffinity);
        helper.setBlock(TARGET, Blocks.STONE);

        //? if >=1.20.5 {
        if (medium != Medium.AIR) {
            var fluid = medium == Medium.WATER ? Blocks.WATER : Blocks.LAVA;
            for (int y = 0; y < 3; y++) helper.setBlock(new BlockPos(0, y, 0), fluid);
        }
        if (vehicle != null && !mount(player)) return;
        helper.getLevel().addFreshEntity(player);
        player.setOnGround(!airborne);
        helper.runAfterDelay(SETTLE_TICKS, () -> {
            // Entity ticking recomputes onGround; restore the stance under test.
            if (vehicle == null) player.setOnGround(!airborne);
            check(player);
        });
        //?} else {
        /*// Before 1.20.5 a mock player added to the world loses its bounding box and crashes on
        // fluid checks, so it stays out of the world unless it has to ride something, and water
        // is simulated by marking its eyes as submerged. Lava has no mining penalty to simulate.
        if (medium == Medium.WATER) LegacyFluids.submergeEyes(helper, player);
        if (vehicle != null) {
            helper.getLevel().addFreshEntity(player);
            if (!mount(player)) return;
        }
        player.setOnGround(!airborne);
        if (vehicle == null) check(player);
        else helper.runAfterDelay(SETTLE_TICKS, () -> check(player));
        *///?}
    }

    private boolean mount(Player player) {
        Entity mount = TestCompat.create(helper, vehicle);
        if (mount == null) {
            TestCompat.fail(helper, name + ": could not create " + vehicle);
            return false;
        }
        // An untamed horse's AI throws its rider off at random, which made the horse tests flaky.
        if (mount instanceof Mob mob) mob.setNoAi(true);
        mount.setPos(player.getX(), player.getY(), player.getZ());
        helper.getLevel().addFreshEntity(mount);
        if (!player.startRiding(mount)) {
            TestCompat.fail(helper, name + ": could not ride " + vehicle);
            return false;
        }
        return true;
    }

    private void check(Player player) {
        if (vehicle != null && !player.isPassenger()) {
            TestCompat.fail(helper, name + ": the player is no longer riding " + vehicle);
            return;
        }
        float expected = expectedSpeed();
        float actual = speed(helper, player);
        if (Math.abs(actual - expected) > expected * TOLERANCE) {
            TestCompat.fail(helper, name + ": expected mining speed " + expected + " but got " + actual
                    + " (onGround=" + player.onGround() + ", passenger=" + player.isPassenger() + ")");
            return;
        }
        helper.succeed();
    }

    /** Taking the helmet off brings the airborne penalty back. */
    static void unequipRemovesBonus(GameTestHelper helper) {
        Player player = TestCompat.mockPlayer(helper);
        BlockPos spawn = helper.absolutePos(BlockPos.ZERO);
        player.setPos(spawn.getX() + 0.5, spawn.getY() + 0.2, spawn.getZ() + 0.5);
        equipHelmet(player, false, true);
        helper.setBlock(TARGET, Blocks.STONE);
        player.setOnGround(false);

        float with = speed(helper, player);
        player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        float without = speed(helper, player);

        if (Math.abs(with - 1.0F) > TOLERANCE || Math.abs(without - PENALTY) > PENALTY * TOLERANCE) {
            TestCompat.fail(helper, "unequip_affinity: expected 1.0 with the helmet and " + PENALTY
                    + " without, got " + with + " and " + without);
            return;
        }
        helper.succeed();
    }

    private static void equipHelmet(Player player, boolean aquaAffinity, boolean flightAffinity) {
        if (!aquaAffinity && !flightAffinity) return;
        ItemStack helmet = new ItemStack(Items.DIAMOND_HELMET);
        TestCompat.enchant(player, helmet, flightAffinity, aquaAffinity);
        player.setItemSlot(EquipmentSlot.HEAD, helmet);
    }

    private static float speed(GameTestHelper helper, Player player) {
        return player.getDestroySpeed(helper.getLevel().getBlockState(helper.absolutePos(TARGET)));
    }
}
