package me.randomhashtags.randomsky.api.unfinished;

import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSStorage;
import me.randomhashtags.randomsky.universal.UInventory;
import me.randomhashtags.randomsky.util.RandomSkyFeature;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public enum CustomEnchants implements RSFeature {
    INSTANCE;

    public YamlConfiguration config;
    private UInventory gemForgeExamine, gemForgeApply, gemForgeRemove;

    @Override
    public @NotNull RandomSkyFeature get_feature() {
        return RandomSkyFeature.CUSTOM_ENCHANTS;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "custom enchants.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "custom enchants.yml"));
        sendConsoleMessage("&6[RandomSky] &aLoaded Custom Enchants &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        RSStorage.unregisterAll(Feature.CUSTOM_ENCHANT, Feature.CUSTOM_ENCHANT_RARITY);
    }
}
