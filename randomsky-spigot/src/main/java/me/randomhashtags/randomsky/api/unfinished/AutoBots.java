package me.randomhashtags.randomsky.api.unfinished;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randomsky.addon.active.ActiveIslandBot;
import me.randomhashtags.randomsky.addon.bot.AutoBotUpgrade;
import me.randomhashtags.randomsky.addon.file.FileAutoBot;
import me.randomhashtags.randomsky.addon.file.FileAutoBotUpgrade;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSStorage;
import me.randomhashtags.randomsky.util.universal.UInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static java.io.File.separator;

public class AutoBots extends RSFeature implements CommandExecutor {
    // TODO: finish this feature
    private static AutoBots instance;
    public static AutoBots getAutoBots() {
        if(instance == null) instance = new AutoBots();
        return instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            viewBots((Player) sender);
        }
        return true;
    }

    public YamlConfiguration config;
    private UInventory gui;
    private HashMap<Player, ActiveIslandBot> viewingbot;
    private HashMap<ActiveIslandBot, List<Player>> editingbotinventory, upgradingbot;

    public void load() {
        final long started = System.currentTimeMillis();
        final String folder = dataFolder + separator + "auto bots";

        save(folder, "_settings.yml");
        if(!otherdata.getBoolean("saved default auto bot upgrades")) {
            final String[] a = new String[]{""};
            for(String s : a) save("auto bot upgrades", s + ".yml");
            otherdata.set("saved default auto bot upgrades", true);
            saveOtherData();
        }
        if(!otherdata.getBoolean("saved default auto bots")) {
            final String[] a = new String[]{"CRAFTING", "MINING", "PLANTER", "SELL"};
            for(String s : a) save("auto bots", s + ".yml");
            otherdata.set("saved default auto bots", true);
            saveOtherData();
        }

        for(File f : new File(folder + separator + "upgrades").listFiles()) {
            new FileAutoBotUpgrade(f);
        }
        for(File f : new File(folder).listFiles()) {
            if(!f.isDirectory() && !f.getAbsoluteFile().getName().equals("_settings.yml")) {
                new FileAutoBot(f);
            }
        }

        gui = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));

        viewingbot = new HashMap<>();
        editingbotinventory = new HashMap<>();
        upgradingbot = new HashMap<>();

        sendConsoleMessage("&6[RandomSky] &aLoaded " + RSStorage.getAll(Feature.AUTO_BOT).size() + " Auto Bots and " + RSStorage.getAll(Feature.AUTO_BOT_UPGRADE).size() + " Auto Bot Upgrades &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        RSStorage.unregisterAll(Feature.AUTO_BOT, Feature.AUTO_BOT_UPGRADE);
    }

    public void viewBots(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.autobot", true)) {
            player.closeInventory();
            player.openInventory(gui.getInventory());
            player.updateInventory();
        }
    }
    public void viewBotInventory(@NotNull Player player, @NotNull ActiveIslandBot bot) {
        player.closeInventory();
        viewingbot.put(player, bot);
        editingbotinventory.put(bot, Arrays.asList(player));
    }
    public void tryUpgrading(@NotNull Player player, @NotNull ActiveIslandBot bot, @NotNull AutoBotUpgrade upgrade) {
        final HashMap<AutoBotUpgrade, Integer> upgrades = bot.getUpgrades();
        if(upgrades.containsKey(upgrade)) {
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerInteractAtEntityEvent(PlayerInteractAtEntityEvent event) {
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        if(event.getView().getTitle().equals(gui.getTitle())) {
        } else if(viewingbot.containsKey(player)) {
            final ActiveIslandBot bot = viewingbot.get(player);
            if(upgradingbot.containsKey(bot)) {
            }
        } else return;
        event.setCancelled(true);
        player.updateInventory();
    }
}
