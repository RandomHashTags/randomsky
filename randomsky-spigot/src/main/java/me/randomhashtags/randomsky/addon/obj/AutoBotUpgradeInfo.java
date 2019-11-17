package me.randomhashtags.randomsky.addon.obj;

import org.bukkit.configuration.file.YamlConfiguration;

import java.math.BigDecimal;
import java.util.HashMap;

public class AutoBotUpgradeInfo {
    private int maxlevel = 1;
    private HashMap<Integer, BigDecimal> values, costs;
    public AutoBotUpgradeInfo(YamlConfiguration yml) {
        values = new HashMap<>();
        costs = new HashMap<>();
        values.put(-1, BigDecimal.valueOf(yml.getDouble("levels.default value")));
        for(String s : yml.getConfigurationSection("levels").getKeys(false)) {
            if(!s.equals("default value")) {
                final int lvl = Integer.parseInt(s);
                values.put(lvl, BigDecimal.valueOf(yml.getDouble("levels." + s + ".value")));
                costs.put(lvl, BigDecimal.valueOf(yml.getDouble("levels." + s + ".cost")));
                maxlevel = lvl;
            }
        }
    }
    public HashMap<Integer, BigDecimal> getValues() { return values; }
    public HashMap<Integer, BigDecimal> getCosts() { return costs; }
    public int getMaxLevel() { return maxlevel; }
}
