package me.randomhashtags.randomsky.api.unfinished;

import me.randomhashtags.randomsky.util.RSFeature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public class LastManStanding extends RSFeature implements CommandExecutor {
    private static LastManStanding instance;
    public static LastManStanding getLastManStanding() {
        if(instance == null) instance = new LastManStanding();
        return instance;
    }

    public YamlConfiguration config;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }

    public void load() {
    }
    public void unload() {
    }
}
