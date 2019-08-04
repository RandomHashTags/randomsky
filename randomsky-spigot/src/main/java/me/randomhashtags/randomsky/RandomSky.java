package me.randomhashtags.randomsky;

import me.randomhashtags.randomsky.api.Homes;
import me.randomhashtags.randomsky.api.ItemFilter;
import me.randomhashtags.randomsky.api.RepairScrolls;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class RandomSky extends JavaPlugin {
    public static RandomSky getPlugin;

    private Homes homes;
    private ItemFilter itemfilter;
    private RepairScrolls repairscrolls;

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

        homes = Homes.getHomes();
        itemfilter = ItemFilter.getItemFilter();
        repairscrolls = RepairScrolls.getRepairScrolls();
    }
    public void disable() {
        Bukkit.getScheduler().cancelTasks(this);
    }
}
