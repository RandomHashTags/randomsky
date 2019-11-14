package me.randomhashtags.randomsky.dev.unfinished;

import me.randomhashtags.randomsky.util.RSFeature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class BattleRoyale extends RSFeature implements CommandExecutor {
    private static BattleRoyale instance;
    public static BattleRoyale getBattleRoyale() {
        if(instance == null) instance = new BattleRoyale();
        return instance;
    }

    public YamlConfiguration config;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final int l = args.length;
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "battle royale.yml");
        config = YamlConfiguration.loadConfiguration(new File(dataFolder, "battle royale.yml"));
        sendConsoleMessage("&6[RandomSky] &aLoaded Battle Royale &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }
}
