package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSStorage;
import org.bukkit.configuration.file.YamlConfiguration;

import static java.io.File.separator;

public class Resources extends RSFeature {
    private static Resources instance;
    public static Resources getIslandResourceNodes() {
        if(instance == null) instance = new Resources();
        return instance;
    }

    public YamlConfiguration config;

    public void load() {
        final long started = System.currentTimeMillis();

        final String[] values = new String[] {
                "saved default resource.fragments",
                "saved default resource.nodes",
                "saved default resource.refined",
                "saved default resource.scraps",
                "saved default resource.unrefined"
        };
        boolean did = false;
        for(String s : values) {
            if(!otherdata.getBoolean(s)) {
                otherdata.set(s, true);
                did = true;
                switch (s) {
                    case "saved default resource.fragments":
                        saveDefaultFragments();
                        break;
                    case "save default resource.nodes":
                        saveDefaultNodes();
                        break;
                    case "save default resource.refined":
                        saveDefaultRefined();
                        break;
                    case "save default resource.scraps":
                        saveDefaultScraps();
                        break;
                    case "save default resource.unrefined":
                        saveDefaultUnrefined();
                        break;
                    default:
                        break;
                }
            }
        }
        if(did) {
            saveOtherData();
        }

        sendConsoleMessage("&6[RandomSky] &aLoaded Resources: " + RSStorage.getAll(Feature.RESOURCE_FRAGMENT).size() + " Fragments, "
                + RSStorage.getAll(Feature.RESOURCE_ITEM).size() + " Items, "
                + RSStorage.getAll(Feature.RESOURCE_REFINED).size() + " Refined, "
                + RSStorage.getAll(Feature.RESOURCE_SCRAP).size() + " Scrap, and "
                + RSStorage.getAll(Feature.RESOURCE_UNREFINED).size() + " Unrefined "
                + "&e(took " + (System.currentTimeMillis()-started) + "ms)"
        );
    }
    public void unload() {
        RSStorage.unregisterAll(Feature.RESOURCE_FRAGMENT, Feature.RESOURCE_ITEM, Feature.RESOURCE_REFINED, Feature.RESOURCE_SCRAP, Feature.RESOURCE_UNREFINED);
    }

    private void saveDefault(String folder, String[] values) {
        for(String s : values) {
            save(folder, s + ".yml");
        }
    }

    public void saveDefaultFragments() { saveDefault(dataFolder + separator + "resources" + separator + "fragments", new String[] { "COBBLESTONE", "GOLD", "LOG", "MOB_SPAWNER" }); }
    public void saveDefaultNodes() { saveDefault(dataFolder + separator + "resources" + separator + "nodes", new String[] { "COAL", "COBBLESTONE", "DIAMOND", "GOLD", "IRON", "LOG" }); }
    public void saveDefaultRefined() { saveDefault(dataFolder + separator + "resources" + separator + "refined", new String[] { "DIAMOND_BLOCK", "IRON_BLOCK" }); }
    public void saveDefaultScraps() { saveDefault(dataFolder + separator + "resources" + separator + "scraps", new String[] { "DIAMOND", "GOLD", "IRON", "OAK", "SCIENCE_GOLD", "STONE" }); }
    public void saveDefaultUnrefined() { saveDefault(dataFolder + separator + "resources" + separator + "unrefined", new String[] { "DIAMOND_BLOCK", "IRON_BLOCK" }); }
}
