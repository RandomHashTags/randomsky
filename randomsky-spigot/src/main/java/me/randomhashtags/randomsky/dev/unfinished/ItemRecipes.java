package me.randomhashtags.randomsky.dev.unfinished;

import me.randomhashtags.randomsky.util.RSFeature;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ItemRecipes extends RSFeature implements CommandExecutor {
    private static ItemRecipes instance;
    public static ItemRecipes getItemRecipes() {
        if(instance == null) instance = new ItemRecipes();
        return instance;
    }

    public YamlConfiguration config;

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "item recipes.yml");

        config = YamlConfiguration.loadConfiguration(new File(rsd, "item recipes.yml"));

        for(String s : config.getConfigurationSection("recipes").getKeys(false)) {
            new ItemRecipe(s);
        }
        final HashMap<String, ItemRecipe> r = ItemRecipe.recipes;
        sendConsoleMessage("&6[RandomSky] &aLoaded " + (r != null ? r.size() : 0) + " Item Recipes &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        ItemRecipe.deleteAll();
    }
}
