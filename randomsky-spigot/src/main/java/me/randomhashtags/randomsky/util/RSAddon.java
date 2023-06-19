package me.randomhashtags.randomsky.util;

import me.randomhashtags.randomsky.universal.UVersionable;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public abstract class RSAddon implements UVersionable {
    protected File file;
    protected YamlConfiguration yml;
    private String ymlName;
    public void load(File file) {
        if(!file.exists()) {
        }
        this.file = file;
        yml = YamlConfiguration.loadConfiguration(file);
    }
    public File getFile() { return file; }
    public YamlConfiguration getYaml() { return yml; }
    public String getYamlName() {
        if(ymlName == null) {
            ymlName = file.getName().split("\\.yml")[0];
        }
        return ymlName;
    }

    public void save() {
        try {
            yml.save(file);
            yml = YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
