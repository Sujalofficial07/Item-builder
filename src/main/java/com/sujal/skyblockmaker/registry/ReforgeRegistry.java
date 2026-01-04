package com.sujal.skyblockmaker.registry;

import com.sujal.skyblockmaker.api.SkyblockStatsApi.StatType;
import java.util.HashMap;
import java.util.Map;

public class ReforgeRegistry {
    
    // Map<ReforgeName, Map<StatType, Value>>
    public static final Map<String, Map<StatType, Double>> REFORGES = new HashMap<>();

    static {
        register("Spicy", StatType.CRIT_DAMAGE, 50.0, StatType.STRENGTH, 10.0, StatType.ATTACK_SPEED, 1.0);
        register("Sharp", StatType.CRIT_CHANCE, 20.0, StatType.CRIT_DAMAGE, 50.0);
        register("Heroic", StatType.INTELLIGENCE, 125.0, StatType.ATTACK_SPEED, 7.0);
        register("Legendary", StatType.STRENGTH, 40.0, StatType.CRIT_CHANCE, 15.0, StatType.INTELLIGENCE, 15.0);
        register("Fabled", StatType.STRENGTH, 60.0, StatType.CRIT_DAMAGE, 40.0);
        register("Ancient", StatType.STRENGTH, 35.0, StatType.CRIT_CHANCE, 15.0, StatType.INTELLIGENCE, 20.0);
        register("Necrotic", StatType.INTELLIGENCE, 150.0);
        register("Withered", StatType.STRENGTH, 140.0);
        register("Giant", StatType.HEALTH, 200.0);
        register("Fierce", StatType.STRENGTH, 10.0, StatType.CRIT_CHANCE, 6.0, StatType.CRIT_DAMAGE, 18.0);
        register("Pure", StatType.SPEED, 4.0, StatType.ATTACK_SPEED, 4.0, StatType.CRIT_CHANCE, 4.0);
    }

    private static void register(String name, Object... stats) {
        Map<StatType, Double> map = new HashMap<>();
        for (int i = 0; i < stats.length; i += 2) {
            map.put((StatType) stats[i], (Double) stats[i+1]);
        }
        REFORGES.put(name, map);
    }

    public static double getBonus(String reforge, StatType stat) {
        if (REFORGES.containsKey(reforge)) {
            return REFORGES.get(reforge).getOrDefault(stat, 0.0);
        }
        return 0;
    }
}
