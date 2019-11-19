package me.randomhashtags.randomsky.api.unfinished;

import me.randomhashtags.randomsky.addon.file.FileInventoryPet;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSStorage;
import org.bukkit.event.Listener;

import java.io.File;

import static java.io.File.separator;

public class InventoryPets extends RSFeature implements Listener {
    private static InventoryPets instance;
    public static InventoryPets getInventoryPets() {
        if(instance == null) instance = new InventoryPets();
        return instance;
    }

    public void load() {
        final long started = System.currentTimeMillis();

        if(!otherdata.getBoolean("saved default pets")) {
            final String[] a = new String[]{"BATTLE PIG", "FARMER BOB"};
            for(String s : a) save("inventory pets", s + ".yml");
            otherdata.set("saved default pets", true);
            saveOtherData();
        }

        for(File f : new File(dataFolder + separator + "inventory pets").listFiles()) {
            new FileInventoryPet(f);
        }
        sendConsoleMessage("&6[RandomSky] &aLoaded " + RSStorage.getAll(Feature.INVENTORY_PET).size() + " Inventory Pets &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        RSStorage.unregisterAll(Feature.INVENTORY_PET);
    }
}
