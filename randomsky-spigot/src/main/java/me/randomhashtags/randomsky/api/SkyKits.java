package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSStorage;
import org.bukkit.event.Listener;

public class SkyKits extends RSFeature implements Listener {
    private static SkyKits instance;
    public static SkyKits getSkyKits() {
        if(instance == null) instance = new SkyKits();
        return instance;
    }
    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomSky] &aLoaded " + RSStorage.getAll(Feature.SKY_KIT).size() + " Sky Kits &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        RSStorage.unregisterAll(Feature.SKY_KIT);
    }
}
