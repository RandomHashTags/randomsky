package me.randomhashtags.randomsky.api.unfinished;

import me.randomhashtags.randomsky.util.RSFeature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Toggles extends RSFeature implements CommandExecutor {
    private static Toggles instance;
    public static Toggles getToggles() {
        if(instance == null) instance = new Toggles();
        return instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }

    public void load() {
    }
    public void unload() {
    }
}
