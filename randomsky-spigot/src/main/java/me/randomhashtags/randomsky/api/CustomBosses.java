package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSStorage;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static java.io.File.separator;

public class CustomBosses extends RSFeature {
    private static CustomBosses instance;
    public static CustomBosses getCustomBosses() {
        if(instance == null) instance = new CustomBosses();
        return instance;
    }

    public YamlConfiguration config;

    public void load() {
        final long started = System.currentTimeMillis();
        final String folder = dataFolder + separator + "custom bosses";
        save(folder, "_settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(folder, "_settings.yml"));

        sendConsoleMessage("&6[RandomSky] &aLoaded " + RSStorage.getAll(Feature.CUSTOM_BOSS).size() + " Custom Bosses &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        RSStorage.unregisterAll(Feature.CUSTOM_BOSS);
    }
}
