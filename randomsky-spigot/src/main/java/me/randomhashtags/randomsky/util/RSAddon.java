package me.randomhashtags.randomsky.util;

import me.randomhashtags.randomsky.RandomSkyAPI;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public abstract class RSAddon extends RSStorage {
    protected RandomSkyAPI api = RandomSkyAPI.getAPI();
    protected File file;
    protected YamlConfiguration yml;
    public void load(File file) {
        if(!file.exists()) {
        }
        this.file = file;
        yml = YamlConfiguration.loadConfiguration(file);
    }
    public File getFile() { return file; }
    public YamlConfiguration getYaml() { return yml; }
    public String getYamlName() { return file.getName().split("\\.yml")[0]; }
}
