package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.addon.PlayerRank;
import me.randomhashtags.randomsky.addon.PlayerSkill;
import me.randomhashtags.randomsky.addon.file.FilePlayerSkill;
import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.supported.economy.Vault;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSPlayer;
import me.randomhashtags.randomsky.util.RSStorage;
import me.randomhashtags.randomsky.util.universal.UInventory;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
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

import static java.io.File.separator;

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
        save(dataFolder + separator + "player ranks", "_settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(dataFolder + separator + "player ranks", "_settings.yml"));

        for(File f : new File(dataFolder + separator + "player ranks").listFiles()) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                final PlayerSkill skill = new FilePlayerSkill(f);
            }
        }

        perm = Vault.getVault().getPermission();

        background = d(config, "gui.background");
        final int size = config.getInt("gui.size");
        gui = new UInventory(null, size, colorize(config.getString("gui.title")));
        final Inventory gi = gui.getInventory();
        for(String s : config.getConfigurationSection("gui").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("background")) {
                final Identifiable i = RSStorage.get(Feature.PLAYER_RANK, config.getString("gui." + s + ".rank"));
                if(i != null) {
                    gi.setItem(config.getInt("gui." + s + ".slot"), ((PlayerRank) i).getItem());
                }
            }
        }
        for(int i = 0; i < size; i++) {
            if(gi.getItem(i) == null) {
                gi.setItem(i, background);
            }
        }

        sendConsoleMessage("&6[RandomSky] &aLoaded " + RSStorage.getAll(Feature.PLAYER_RANK).size() + " Player Ranks &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        RSStorage.unregisterAll(Feature.PLAYER_RANK);
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
            if(pr == null || p.getRankValue() > pr.getRankValue()) {
                pdata.setRank(p);
            } else {
                return;
            }

            removeItem(player, is, 1);
            for(String s : p.getAttributes()) {
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
        if(top.getHolder() == player && event.getView().getTitle().equals(gui.getTitle())) {
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
