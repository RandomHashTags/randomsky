package me.randomhashtags.randomsky.api.ready;

import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSStorage;
import me.randomhashtags.randomsky.util.universal.UInventory;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class CustomEnchants extends RSFeature {
    private static CustomEnchants instance;
    public static CustomEnchants getCustomEnchants() {
        if(instance == null) instance = new CustomEnchants();
        return instance;
    }

    public YamlConfiguration config;
    private UInventory gemForgeExamine, gemForgeApply, gemForgeRemove;

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "custom enchants.yml");
        config = YamlConfiguration.loadConfiguration(new File(dataFolder, "custom enchants.yml"));
        sendConsoleMessage("&6[RandomSky] &aLoaded Custom Enchants &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        RSStorage.unregisterAll(Feature.CUSTOM_ENCHANT, Feature.CUSTOM_ENCHANT_RARITY);
    }
}
