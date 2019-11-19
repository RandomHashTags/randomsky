package me.randomhashtags.randomsky.api.unfinished;

import me.randomhashtags.randomsky.addon.file.FileItemRecipe;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSStorage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static java.io.File.separator;

public class ItemRecipes extends RSFeature implements CommandExecutor {
    private static ItemRecipes instance;
    public static ItemRecipes getItemRecipes() {
        if(instance == null) instance = new ItemRecipes();
        return instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }

    public YamlConfiguration config;

    public void load() {
        final long started = System.currentTimeMillis();
        save(dataFolder + separator + "item recipes", "_settings.yml");

        config = YamlConfiguration.loadConfiguration(new File(dataFolder + separator + "item recipes", "_settings.yml"));

        for(File f : new File(dataFolder + separator + "item recipes").listFiles()) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                new FileItemRecipe(f);
            }
        }
        sendConsoleMessage("&6[RandomSky] &aLoaded " + RSStorage.getAll(Feature.ITEM_RECIPE).size() + " Item Recipes &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        RSStorage.unregisterAll(Feature.ITEM_RECIPE);
    }
}
