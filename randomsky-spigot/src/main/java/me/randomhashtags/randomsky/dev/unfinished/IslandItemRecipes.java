package me.randomhashtags.randomsky.dev.unfinished;

import me.randomhashtags.randomsky.dev.ItemRecipe;
import me.randomhashtags.randomsky.util.RSFeature;
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
        config = YamlConfiguration.loadConfiguration(new File(rsd, "island farming.yml"));

        int loaded = 0;
        for(String s : config.getConfigurationSection("").getKeys(false)) {
        }
        sendConsoleMessage("&6[RandomSky] &aLoaded " + loaded + " Island Item Recipes &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        ItemRecipe.deleteAll();
    }
}
