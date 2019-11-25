package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSStorage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public class Realms extends RSFeature implements CommandExecutor {
    private static Realms instance;
    public static Realms getRealms() {
        if(instance == null) instance = new Realms();
        return instance;
    }

    public YamlConfiguration config;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomSky] &aLoaded " + RSStorage.getAll(Feature.REALM).size() + " Realms &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        RSStorage.unregisterAll(Feature.REALM);
    }
}
