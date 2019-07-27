package me.randomhashtags.randomsky;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class RandomSky extends JavaPlugin {

    public static RandomSky getPlugin;
    public void onEnable() {
        enable();
    }

    public void onDisable() {
        disable();
    }

    public void reload() {
        disable();
        enable();
    }


    public void enable() {
        getPlugin = this;
    }
    public void disable() {
        Bukkit.getScheduler().cancelTasks(this);
    }
}
