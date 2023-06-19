package me.randomhashtags.randomsky.api.skill;

import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RandomSkyFeature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public enum IslandFishing implements RSFeature, CommandExecutor {
    INSTANCE;

    @Override
    public @NotNull RandomSkyFeature get_feature() {
        return RandomSkyFeature.ISLAND_FISHING;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }

    public YamlConfiguration config;

    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomSky] &aLoaded Island Fishing &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }
}
