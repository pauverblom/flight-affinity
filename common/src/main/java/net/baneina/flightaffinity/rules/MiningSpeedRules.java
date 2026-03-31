package net.baneina.flightaffinity.rules;

/**
 * Encapsulates rules regarding the mining speed logic within different environments and stances.
 */
public final class MiningSpeedRules {

    /**
     * Environment states for mining.
     */
    public enum Environment {
        /** In air. */
        AIR,
        /** In water. */
        WATER,
        /** In other fluids like lava. */
        OTHER_FLUID
    }

    /**
     * Stance statuses for the player.
     */
    public enum Stance {
        /** Grounded stance. */
        GROUNDED,
        /** Airborne stance. */
        AIRBORNE
    }

    /**
     * Private constructor for utility class.
     */
    private MiningSpeedRules() {
    }

    /**
     * Determines if a player should be treated as on the ground for mining speed purposes.
     * @param environment the environment the player is in
     * @param stance the stance of the player
     * @param hasFlightAffinity true if the player has the Flight Affinity enchantment
     * @return true if the player should be treated as on the ground
     */
    public static boolean shouldTreatAsOnGround(Environment environment, Stance stance, boolean hasFlightAffinity) {
        if (stance == Stance.GROUNDED) {
            return true;
        }
        return hasFlightAffinity;
    }

    /**
     * Calculates the final mining speed percentage based on the rules.
     * @param environment the environment the player is in
     * @param stance the stance of the player
     * @param hasAquaAffinity true if the player has the Aqua Affinity enchantment
     * @param hasFlightAffinity true if the player has the Flight Affinity enchantment
     * @return the calculated mining speed percentage
     */
    public static int finalMiningSpeedPercent(Environment environment, Stance stance, boolean hasAquaAffinity, boolean hasFlightAffinity) {
        int speed = 100;
        boolean airbornePenalty = stance == Stance.AIRBORNE && !hasFlightAffinity;
        boolean waterPenalty = environment == Environment.WATER && !hasAquaAffinity;

        if (airbornePenalty) {
            speed /= 5;
        }
        if (waterPenalty) {
            speed /= 5;
        }
        return speed;
    }
}
