package me.randomhashtags.randomsky.api.unfinished;

import me.randomhashtags.randomsky.util.RSFeature;
import org.bukkit.configuration.file.YamlConfiguration;

public class EnchantmentTables extends RSFeature {
    private static EnchantmentTables instance;
    public static EnchantmentTables getEnchantmentTables() {
        if(instance == null) instance = new EnchantmentTables();
        return instance;
    }

    public YamlConfiguration config;

    public void load() {
        final long started = System.currentTimeMillis();

        sendConsoleMessage("&6[RandomSky] &aLoaded Enchantment Tables &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }
}
