package me.randomhashtags.randomsky.dev.unfinished;

import me.randomhashtags.randomsky.util.RSFeature;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;

public class AutoBots extends RSFeature implements CommandExecutor {
    private static AutoBots instance;
    public static AutoBots getAutoBots() {
        if(instance == null) instance = new AutoBots();
        return instance;
    }

    public YamlConfiguration config;

    public void load() {}
    public void unload() {}
}
