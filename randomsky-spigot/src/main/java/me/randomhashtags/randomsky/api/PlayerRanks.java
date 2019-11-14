package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.addon.PlayerRank;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSPlayer;
import me.randomhashtags.randomsky.util.universal.UInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class PlayerRanks extends RSFeature implements CommandExecutor {
    private static PlayerRanks instance;
    public static PlayerRanks getPlayerRanks() {
        if(instance == null) instance = new PlayerRanks();
        return instance;
    }

    public YamlConfiguration config;
    private UInventory gui;
    private ItemStack background;
    private Permission perm;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) return true;
        final Player player = (Player) sender;
        viewRanks(player);
        return true;
    }
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "player ranks.yml");
        config = YamlConfiguration.loadConfiguration(new File(randomsky.getDataFolder(), "player ranks.yml"));
        int loaded = 0;
        for(String s : config.getConfigurationSection("ranks").getKeys(false)) {
            final String p = "ranks." + s + ".";
            new PlayerRank(s, colorize(config.getString(p + "appearance")), d(config, "ranks." + s), config.getStringList(p + "attributes"));
            loaded++;
        }

        perm = VaultAPI.getVaultAPI().perms;

        background = d(config, "gui.background");
        final int size = config.getInt("gui.size");
        gui = new UInventory(null, size, colorize(config.getString("gui.title")));
        final Inventory gi = gui.getInventory();
        for(String s : config.getConfigurationSection("gui").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("background")) {
                final PlayerRank r = PlayerRank.paths.getOrDefault(config.getString("gui." + s + ".rank"), null);
                if(r != null) {
                    gi.setItem(config.getInt("gui." + s + ".slot"), r.item());
                }
            }
        }
        for(int i = 0; i < size; i++) {
            if(gi.getItem(i) == null) {
                gi.setItem(i, background);
            }
        }

        sendConsoleMessage("&6[RandomSky] &aLoaded " + loaded + " Player Ranks &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        playerranks = null;
    }

    public void viewRanks(Player player) {
        if(hasPermission(player, "RandomSky.ranks.view", true)) {
            player.closeInventory();
            final int size = gui.getSize();
            player.openInventory(Bukkit.createInventory(player, size, gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());
            for(int i = 0; i < size; i++) {
            }
            player.updateInventory();
        }
    }

    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack is = event.getItem();
        final PlayerRank p = PlayerRank.valueOf(is);
        if(p != null) {
            final Player player = event.getPlayer();
            final RSPlayer pdata = RSPlayer.get(player.getUniqueId());
            final PlayerRank pr = pdata.getRank();
            event.setCancelled(true);
            player.updateInventory();
            if(pr == null || p.priority > pr.priority) {
                pdata.setRank(p);
            } else {
                return;
            }

            removeItem(player, is, 1);
            for(String s : p.attributes) {
                final String S = s.toLowerCase();
                if(S.startsWith("addperm{")) {
                    final String P = s.split("\\{")[1].split("}")[0];
                    if(perm != null && !perm.has(player, P)) {
                        perm.playerAdd(player, P);
                    }
                } else if(S.startsWith("redeem{")) {
                    final String a = s.split("Redeem\\{")[1], A = a.toLowerCase();
                    if(A.startsWith("giveitem")) {
                        final ItemStack i = d(null, a.split("\\{")[1].split("}")[0]);
                        if(i != null) {
                            giveItem(player, i);
                        }
                    }
                }
            }
            player.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        if(top.getHolder() == player && top.getTitle().equals(gui.getTitle())) {
            event.setCancelled(true);
            player.updateInventory();
            final int r = event.getRawSlot();
            final ItemStack c = event.getCurrentItem();
            if(r < 0 || r >= top.getSize() || c == null || c.getType().equals(Material.AIR)) return;
            final PlayerRank pr = PlayerRank.valueOf(c);
            if(pr != null) {
                sendStringListMessage(player, config.getStringList("messages.unlock rank"), null);
            }
        }
    }
}
