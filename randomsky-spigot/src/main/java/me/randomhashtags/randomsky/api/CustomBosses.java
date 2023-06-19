package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSStorage;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static java.io.File.separator;

public enum CustomBosses implements RSFeature {
    INSTANCE;

    public YamlConfiguration config;

    @Override
    public @NotNull Feature get_feature() {
        return Feature.CUSTOM_BOSS;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        final String folder = DATA_FOLDER + separator + "custom bosses";
        save(folder, "_settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(folder, "_settings.yml"));

        sendConsoleMessage("&6[RandomSky] &aLoaded " + RSStorage.getAll(Feature.CUSTOM_BOSS).size() + " Custom Bosses &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        RSStorage.unregisterAll(Feature.CUSTOM_BOSS);
    }
}
