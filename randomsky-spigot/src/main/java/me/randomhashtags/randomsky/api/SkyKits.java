package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSStorage;
import me.randomhashtags.randomsky.util.RandomSkyFeature;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public enum SkyKits implements RSFeature, Listener {
    INSTANCE;

    @Override
    public @NotNull RandomSkyFeature get_feature() {
        return RandomSkyFeature.SKY_KITS;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomSky] &aLoaded " + RSStorage.getAll(Feature.SKY_KIT).size() + " Sky Kits &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        RSStorage.unregisterAll(Feature.SKY_KIT);
    }
}
