package me.randomhashtags.randomsky.api.unfinished;

import me.randomhashtags.randomsky.util.RSFeature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public class IslandFishing extends RSFeature implements CommandExecutor {
    private static IslandFishing instance;
    public static IslandFishing getIslandFishing() {
        if(instance == null) instance = new IslandFishing();
        return instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }

    public YamlConfiguration config;
    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomSky] &aLoaded Island Fishing &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }
}
