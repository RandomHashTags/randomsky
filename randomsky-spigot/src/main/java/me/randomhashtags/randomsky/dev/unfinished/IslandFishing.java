package me.randomhashtags.randomsky.dev.unfinished;

import me.randomhashtags.randomsky.util.RSFeature;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;

public class IslandFishing extends RSFeature implements CommandExecutor {
    private static IslandFishing instance;
    public static IslandFishing getIslandFishing() {
        if(instance == null) instance = new IslandFishing();
        return instance;
    }
    public YamlConfiguration config;
    public void load() {}
    public void unload() {}
}
