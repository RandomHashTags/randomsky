package me.randomhashtags.randomsky.dev.unfinished;

import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.newRSStorage;
import org.bukkit.event.Listener;

import java.io.File;

import static java.io.File.separator;

public class Pets extends RSFeature implements Listener {
    private static Pets instance;
    public static Pets getPets() {
        if(instance == null) instance = new Pets();
        return instance;
    }

    public void load() {
        final long started = System.currentTimeMillis();

        if(!otherdata.getBoolean("saved default pets")) {
            final String[] a = new String[]{"BATTLE PIG", "FARMER BOB"};
            for(String s : a) save("pets", s + ".yml");
            otherdata.set("saved default pets", true);
            saveOtherData();
        }

        for(File f : new File(dataFolder + separator + "pets").listFiles()) {
            new Pet(f);
        }
        final HashMap<String, Pet> p = Pet.pets;
        sendConsoleMessage("&6[RandomSky] &aLoaded " + newRSStorage.getAll(Feature.PETS).size() + " Pets &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        newRSStorage.unregisterAll(Feature.PETS);
    }
}
