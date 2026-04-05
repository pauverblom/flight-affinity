package net.baneina.flightaffinity.rules;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Documents and verifies the vanilla mining-speed penalty model.
 *
 * <p>Vanilla applies two independent ×0.2 penalties to mining speed:
 * <ul>
 *     <li><b>Airborne penalty</b> — applied when not on ground; canceled by Flight Affinity.</li>
 *     <li><b>Water penalty</b> — applied when submerged in water; canceled by Aqua Affinity.</li>
 * </ul>
 * Both penalties stack multiplicatively (worst case ×0.04).
 */
class MiningSpeedRulesTest {

    /** Tolerance for JUnit float comparisons. */
    private static final float TOLERANCE = 0.0001F;

    /** Combined multiplier when no penalty is active (×1.0). */
    private static final float NO_PENALTY = 1.0F;

    /** Combined multiplier when one penalty is active (×0.2). */
    private static final float SINGLE_PENALTY = 0.2F;

    /** Combined multiplier when both penalties are active (×0.04). */
    private static final float DOUBLE_PENALTY = 0.04F;

    // -------------------------------------------------------------------------
    // Penalty model
    // -------------------------------------------------------------------------

    private enum Environment {
        /** Normal air — no fluid penalty. */
        AIR,
        /** Submerged in water — fluid penalty applies unless Aqua Affinity is present. */
        WATER,
        /**
         * Submerged in lava or a modded fluid.
         * Currently treated identically to {@link #AIR} (no fluid penalty).
         * Reserved for potential future behavior.
         */
        LAVA_OR_MODDED
    }

    private enum Stance {
        /** On solid ground — no airborne penalty. */
        GROUNDED,
        /** In the air — airborne penalty applies unless Flight Affinity is present. */
        AIRBORNE
    }

    private static float resolveMultiplier(
            Environment environment,
            Stance stance,
            boolean hasAquaAffinity,
            boolean hasFlightAffinity
    ) {
        float fluidFactor = hasFluidPenalty(environment, hasAquaAffinity)
                ? SINGLE_PENALTY : NO_PENALTY;
        float airborneFactor = hasAirbornePenalty(stance, hasFlightAffinity)
                ? SINGLE_PENALTY : NO_PENALTY;
        return fluidFactor * airborneFactor;
    }

    private static boolean hasFluidPenalty(Environment environment, boolean hasAquaAffinity) {
        return environment == Environment.WATER && !hasAquaAffinity;
    }

    private static boolean hasAirbornePenalty(Stance stance, boolean hasFlightAffinity) {
        return stance == Stance.AIRBORNE && !hasFlightAffinity;
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    void shouldMatchThe24CaseMatrix() {
        assertMultiplier("1",  Environment.AIR,          Stance.GROUNDED,  false, false, NO_PENALTY);
        assertMultiplier("2",  Environment.AIR,          Stance.GROUNDED,  true,  false, NO_PENALTY);
        assertMultiplier("3",  Environment.AIR,          Stance.GROUNDED,  false, true,  NO_PENALTY);
        assertMultiplier("4",  Environment.AIR,          Stance.GROUNDED,  true,  true,  NO_PENALTY);

        assertMultiplier("5",  Environment.AIR,          Stance.AIRBORNE,  false, false, SINGLE_PENALTY);
        assertMultiplier("6",  Environment.AIR,          Stance.AIRBORNE,  true,  false, SINGLE_PENALTY);
        assertMultiplier("7",  Environment.AIR,          Stance.AIRBORNE,  false, true,  NO_PENALTY);
        assertMultiplier("8",  Environment.AIR,          Stance.AIRBORNE,  true,  true,  NO_PENALTY);

        assertMultiplier("9",  Environment.WATER,        Stance.GROUNDED,  false, false, SINGLE_PENALTY);
        assertMultiplier("10", Environment.WATER,        Stance.GROUNDED,  true,  false, NO_PENALTY);
        assertMultiplier("11", Environment.WATER,        Stance.GROUNDED,  false, true,  SINGLE_PENALTY);
        assertMultiplier("12", Environment.WATER,        Stance.GROUNDED,  true,  true,  NO_PENALTY);

        assertMultiplier("13", Environment.WATER,        Stance.AIRBORNE,  false, false, DOUBLE_PENALTY);
        assertMultiplier("14", Environment.WATER,        Stance.AIRBORNE,  true,  false, SINGLE_PENALTY);
        assertMultiplier("15", Environment.WATER,        Stance.AIRBORNE,  false, true,  SINGLE_PENALTY);
        assertMultiplier("16", Environment.WATER,        Stance.AIRBORNE,  true,  true,  NO_PENALTY);

        assertMultiplier("17", Environment.LAVA_OR_MODDED, Stance.GROUNDED, false, false, NO_PENALTY);
        assertMultiplier("18", Environment.LAVA_OR_MODDED, Stance.GROUNDED, true,  false, NO_PENALTY);
        assertMultiplier("19", Environment.LAVA_OR_MODDED, Stance.GROUNDED, false, true,  NO_PENALTY);
        assertMultiplier("20", Environment.LAVA_OR_MODDED, Stance.GROUNDED, true,  true,  NO_PENALTY);

        assertMultiplier("21", Environment.LAVA_OR_MODDED, Stance.AIRBORNE, false, false, SINGLE_PENALTY);
        assertMultiplier("22", Environment.LAVA_OR_MODDED, Stance.AIRBORNE, true,  false, SINGLE_PENALTY);
        assertMultiplier("23", Environment.LAVA_OR_MODDED, Stance.AIRBORNE, false, true,  NO_PENALTY);
        assertMultiplier("24", Environment.LAVA_OR_MODDED, Stance.AIRBORNE, true,  true,  NO_PENALTY);
    }

    private static void assertMultiplier(
            String caseId,
            Environment environment,
            Stance stance,
            boolean hasAquaAffinity,
            boolean hasFlightAffinity,
            float expected
    ) {
        float actual = resolveMultiplier(environment, stance, hasAquaAffinity, hasFlightAffinity);
        assertEquals(expected, actual, TOLERANCE, "case " + caseId);
    }
}

