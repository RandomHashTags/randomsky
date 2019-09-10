package me.randomhashtags.randomsky.dev.unfinished;

import me.randomhashtags.randomsky.util.RSFeature;
import org.bukkit.configuration.file.YamlConfiguration;

public class EnchantmentTables extends RSFeature {
    private static EnchantmentTables instance;
    public static EnchantmentTables getEnchantmentTables() {
        if(instance == null) instance = new EnchantmentTables();
        return instance;
    }

    public YamlConfiguration config;

    public void load() {}
    public void unload() {}
}
