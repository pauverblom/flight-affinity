package net.baneina.flightaffinity.test;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
//? if <1.21.5 {
/*import net.minecraft.gametest.framework.TestFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
*///?}

/**
 * Every GameTest, by name. Each loader registers this map (see the fabric, neoforge and forge
 * subpackages), so adding a test only means adding it here. From 1.21.5 on each test also needs
 * a data/flightaffinity-test/test_instance/<name>.json file.
 */
public final class FlightAffinityTests {
    public static final String NAMESPACE = "flightaffinity-test";
    public static final Map<String, Consumer<GameTestHelper>> ALL = new LinkedHashMap<>();

    static {
        // Mining speed for every combination of medium, stance, Aqua Affinity and Flight Affinity.
        for (MiningScenario.Medium medium : MiningScenario.Medium.values()) {
            for (boolean airborne : new boolean[]{false, true}) {
                for (boolean aqua : new boolean[]{false, true}) {
                    for (boolean flight : new boolean[]{false, true}) {
                        String name = medium.name().toLowerCase() + (airborne ? "_airborne_" : "_grounded_")
                                + (aqua ? (flight ? "both" : "aqua") : (flight ? "flight" : "none"));
                        ALL.put(name, helper -> new MiningScenario(helper, name)
                                .in(medium).airborne(airborne).aquaAffinity(aqua).flightAffinity(flight).run());
                    }
                }
            }
        }

        // Riding counts as airborne in vanilla; Flight Affinity removes that penalty too.
        ALL.put("vehicle_minecart_no", helper -> new MiningScenario(helper, "vehicle_minecart_no")
                .riding(EntityType.MINECART).run());
        ALL.put("vehicle_minecart_yes", helper -> new MiningScenario(helper, "vehicle_minecart_yes")
                .riding(EntityType.MINECART).flightAffinity(true).run());
        ALL.put("vehicle_horse_no", helper -> new MiningScenario(helper, "vehicle_horse_no")
                .riding(EntityType.HORSE).run());
        ALL.put("vehicle_horse_yes", helper -> new MiningScenario(helper, "vehicle_horse_yes")
                .riding(EntityType.HORSE).flightAffinity(true).run());

        ALL.put("unequip_affinity", MiningScenario::unequipRemovesBonus);
        ALL.put("spawns_properly", TestCompat::enchantmentIsObtainable);
    }

    private FlightAffinityTests() {}

    //? if <1.21.5 {
    /*// Before 1.21.5 tests are TestFunctions handed out by a @GameTestGenerator method.
    public static Collection<TestFunction> testFunctions() {
        List<TestFunction> functions = new ArrayList<>();
        ALL.forEach((name, test) -> functions.add(
                new TestFunction(NAMESPACE, NAMESPACE + "." + name, NAMESPACE + ":empty", 100, 0, true, test)));
        return functions;
    }
    *///?}
}
