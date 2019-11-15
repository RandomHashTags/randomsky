package me.randomhashtags.randomsky;

import me.randomhashtags.randomsky.api.fixed.Homes;
import me.randomhashtags.randomsky.api.fixed.ItemFilter;
import me.randomhashtags.randomsky.api.fixed.RepairScrolls;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
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
        HandlerList.unregisterAll(this);
    }
}
