package me.randomhashtags.randomsky;

import me.randomhashtags.randomsky.api.Homes;
import me.randomhashtags.randomsky.api.ItemFilter;
import me.randomhashtags.randomsky.api.RepairScrolls;
import me.randomhashtags.randomsky.universal.UVersionable;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class RandomSky extends JavaPlugin {
    public static RandomSky getPlugin;

    private Homes homes;
    private ItemFilter itemfilter;
    private RepairScrolls repairscrolls;

    public boolean mcmmo_is_enabled;
    public boolean placeholder_api_is_enabled;

    @Override
    public void onEnable() {
        enable();
    }
    @Override
    public void onDisable() {
        disable();
    }

    public void reload() {
        disable();
        enable();
    }


    public void enable() {
        getPlugin = this;
        mcmmo_is_enabled = UVersionable.PLUGIN_MANAGER.isPluginEnabled("mcMMO");
        placeholder_api_is_enabled = UVersionable.PLUGIN_MANAGER.isPluginEnabled("PlaceholderAPI");

        homes = Homes.INSTANCE;
        itemfilter = ItemFilter.INSTANCE;
        repairscrolls = RepairScrolls.INSTANCE;
    }
    public void disable() {
        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);
    }
}
