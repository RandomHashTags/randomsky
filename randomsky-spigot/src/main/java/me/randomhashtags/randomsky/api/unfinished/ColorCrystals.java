package me.randomhashtags.randomsky.api.unfinished;

import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSStorage;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ColorCrystals extends RSFeature {
    private static ColorCrystals instance;
    public static ColorCrystals getColorCrystals() {
        if(instance == null) instance = new ColorCrystals();
        return instance;
    }

    public YamlConfiguration config;

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "color crystals.yml");
        config = YamlConfiguration.loadConfiguration(new File(dataFolder, "color crystals.yml"));
        sendConsoleMessage("&6[RandomSky] &aLoaded " + RSStorage.getAll(Feature.COLOR_CRYSTAL).size() + " Color Crystals &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        RSStorage.unregisterAll(Feature.COLOR_CRYSTAL);
    }
}
