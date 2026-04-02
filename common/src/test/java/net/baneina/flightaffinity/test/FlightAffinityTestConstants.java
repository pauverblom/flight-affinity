package net.baneina.flightaffinity.test;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class FlightAffinityTestConstants {

    // -- Game setup -----------------------------------------------------------

    /** Ticks to wait after spawning before measuring speed. */
    public static final int TICK_DELAY = 2;

    public static final Item HELMET_TYPE = Items.DIAMOND_HELMET;
    public static final Block DEFAULT_TARGET_BLOCK = Blocks.STONE;

    // -- Exact penalty values (used by the JUnit matrix) ----------------------

    /** Tolerance for JUnit float comparisons. */
    public static final float TOLERANCE = 0.0001F;

    /** Combined multiplier when no penalty is active (×1.0). */
    public static final float NO_PENALTY = 1.0F;

    /** Combined multiplier when one penalty is active (×0.2). */
    public static final float SINGLE_PENALTY = 0.2F;

    /** Combined multiplier when both penalties are active (×0.04). */
    public static final float DOUBLE_PENALTY = 0.04F;

    // -- In-game speed bands (used by GameTests) ------------------------------

    /** Accepted range for ×1.0 (no penalties). */
    public static final float NO_PENALTY_MIN = 0.99F;
    public static final float NO_PENALTY_MAX = 1.01F;

    /** Accepted range for ×0.2 (one penalty). */
    public static final float SINGLE_PENALTY_MIN = 0.19F;
    public static final float SINGLE_PENALTY_MAX = 0.21F;

    /** Accepted range for ×0.04 (two penalties). */
    public static final float DOUBLE_PENALTY_MIN = 0.039F;
    public static final float DOUBLE_PENALTY_MAX = 0.041F;
}
