package me.randomhashtags.randomsky.dev.unfinished;

import me.randomhashtags.randomsky.addon.active.ActiveIslandBot;
import me.randomhashtags.randomsky.addon.bot.AutoBotUpgrade;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.universal.UInventory;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
        save(null, "auto bots/_settings.yml");
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

        gui = new UInventory(null, config.getInt("gui.size"), ChatColor.translateAlternateColorCodes('&', config.getString("gui.title")));

        viewingbot = new HashMap<>();
        editingbotinventory = new HashMap<>();
        upgradingbot = new HashMap<>();

        sendConsoleMessage("&6[RandomSky] &aLoaded " + (autobots != null ? autobots.size() : 0) + " Auto Bots and (" + (autobotupgrades != null ? autobotupgrades.size() : 0) + ") Auto Bot Upgrades &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        autobots = null;
        autobotupgrades = null;
    }

    public void viewBots(Player player) {
        if(hasPermission(player, "RandomSky.autobot", true)) {
            player.closeInventory();
            player.openInventory(gui.getInventory());
        }
    }
    public void viewBotInventory(Player player, ActiveIslandBot bot) {
        player.closeInventory();
        viewingbot.put(player, bot);
        editingbotinventory.put(bot, Arrays.asList(player));
    }
    public void tryUpgrading(Player player, ActiveIslandBot bot, AutoBotUpgrade upgrade) {
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
