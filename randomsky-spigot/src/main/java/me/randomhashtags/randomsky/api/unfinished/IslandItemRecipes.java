package me.randomhashtags.randomsky.api.unfinished;

import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSStorage;
import me.randomhashtags.randomsky.util.universal.UInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class IslandItemRecipes extends RSFeature implements CommandExecutor {
    private static IslandItemRecipes instance;
    public static IslandItemRecipes getIslandItemRecipes() {
        if(instance == null) instance = new IslandItemRecipes();
        return instance;
    }

    public YamlConfiguration config;
    private UInventory gui;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "island farming.yml");
        config = YamlConfiguration.loadConfiguration(new File(dataFolder, "island farming.yml"));

        sendConsoleMessage("&6[RandomSky] &aLoaded " + RSStorage.getAll(Feature.ISLAND_ITEM_RECIPE).size() + " Island Item Recipes &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        RSStorage.unregisterAll(Feature.ISLAND_ITEM_RECIPE);
    }
}
