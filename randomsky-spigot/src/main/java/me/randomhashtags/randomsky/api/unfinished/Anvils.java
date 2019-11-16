package me.randomhashtags.randomsky.api.unfinished;

import me.randomhashtags.randomsky.util.RSFeature;
import org.bukkit.configuration.file.YamlConfiguration;

public class Anvils extends RSFeature {
    private static Anvils instance;
    public static Anvils getAnvils() {
        if(instance == null) instance = new Anvils();
        return instance;
    }

    public YamlConfiguration config;

    public void load() {}
    public void unload() {}
}
