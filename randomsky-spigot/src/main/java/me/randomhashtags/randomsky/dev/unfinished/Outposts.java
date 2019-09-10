package me.randomhashtags.randomsky.dev.unfinished;

import me.randomhashtags.randomsky.util.RSFeature;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;

public class Outposts extends RSFeature implements CommandExecutor {
    private static Outposts instance;
    public static Outposts getOutposts() {
        if(instance == null) instance = new Outposts();
        return instance;
    }

    public YamlConfiguration config;

    public void load() {}
    public void unload() {}
}
