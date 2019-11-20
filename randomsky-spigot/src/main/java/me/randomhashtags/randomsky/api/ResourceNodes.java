package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.addon.file.FileResourceNode;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSStorage;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static java.io.File.separator;

public class ResourceNodes extends RSFeature {
    private static ResourceNodes instance;
    public static ResourceNodes getResourceNodes() {
        if(instance == null) instance = new ResourceNodes();
        return instance;
    }

    public YamlConfiguration config;

    public void load() {
        final long started = System.currentTimeMillis();
        save("resources" + separator + "nodes", "_settings.yml");
        final String folder = dataFolder + separator + "resources" + separator + "nodes";
        config = YamlConfiguration.loadConfiguration(new File(folder, "_settings.yml"));

        for(File f : new File(folder).listFiles()) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                new FileResourceNode(f);
            }
        }

        sendConsoleMessage("&6[RandomSky] &aLoaded " + RSStorage.getAll(Feature.RESOURCE_NODE).size() + " Resource Nodes &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        RSStorage.unregisterAll(Feature.RESOURCE_NODE);
    }
}
