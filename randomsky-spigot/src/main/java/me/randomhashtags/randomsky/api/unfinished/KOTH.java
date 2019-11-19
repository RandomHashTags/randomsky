package me.randomhashtags.randomsky.api.unfinished;

import me.randomhashtags.randomsky.util.RSFeature;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;

public class KOTH extends RSFeature implements CommandExecutor {
    private static KOTH instance;
    public static KOTH getKOTH() {
        if(instance == null) instance = new KOTH();
        return instance;
    }

    public YamlConfiguration config;

    public void load() {
    }
    public void unload() {
    }
}
