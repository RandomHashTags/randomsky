package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.addon.adventure.Adventure;
import me.randomhashtags.randomsky.addon.adventure.AdventureMap;
import me.randomhashtags.randomsky.addon.file.FileAdventure;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSPlayer;
import me.randomhashtags.randomsky.util.RSStorage;
import me.randomhashtags.randomsky.util.universal.UInventory;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.io.File.separator;

public class Adventures extends RSFeature implements CommandExecutor {
    private static Adventures instance;
    public static Adventures getAdventures() {
        if(instance == null) instance = new Adventures();
        return instance;
    }

    public YamlConfiguration config;

    private UInventory gui;
    private ItemStack mapRequired;
    private List<World> worlds;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final int l = args.length;
        if(l == 0) {
            viewAdventures(player);
        } else {
            final String a = args[0];
            if(a.equals("help")) {
                viewHelp(sender);
            }
        }
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "adventures.yml");
        if(!otherdata.getBoolean("saved default adventures")) {
            final String[] a = new String[] {
                    "ABANDONED_RUINS", "DEMONIC_REALM", "LOST_WASTELAND"
            };
            for(String s : a) {
                save("adventures", s + ".yml");
            }
            otherdata.set("saved default adventures", true);
            saveOtherData();
        }
        config = YamlConfiguration.loadConfiguration(new File(randomsky.getDataFolder(), "adventures.yml"));

        worlds = new ArrayList<>();

        mapRequired = d(config, "gui.map required");
        gui = new UInventory(null, config.getInt("gui.size"), ChatColor.translateAlternateColorCodes('&', config.getString("gui.title")));
        final Inventory gi = gui.getInventory();
        final ItemStack b = d(config, "gui.background");
        for(File f : new File(dataFolder + separator + "adventures").listFiles()) {
            final Adventure a = new FileAdventure(f);
            gi.setItem(a.getSlot(), a.getItem());
            worlds.add(toLocation(a.getTeleportLocations().get(0)).getWorld());
        }

        for(int i = 0; i < gui.getSize(); i++) {
            if(gi.getItem(i) == null) {
                gi.setItem(i, b);
            }
        }
        sendConsoleMessage("&6[RandomSky] &aLoaded " + RSStorage.getAll(Feature.ADVENTURE).size() + " Adventures &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        RSStorage.unregisterAll(Feature.ADVENTURE, Feature.ADVENTURE_MAP, Feature.ADVENTURE_MAP_FRAGMENT);
    }

    public void viewHelp(CommandSender sender) {
        if(hasPermission(sender, "RandomSky.adventure.help", true)) {
            sendStringListMessage(sender, config.getStringList("messages.help"), null);
        }
    }

    public void viewAdventures(Player player) {
        if(hasPermission(player, "RandomSky.adventure.view", true)) {
            final List<Adventure> allowed = RSPlayer.get(player.getUniqueId()).getAllowedAdventures();
            final int size = gui.getSize();
            player.openInventory(Bukkit.createInventory(player, size, gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());
            for(int i = 0; i < size; i++) {
                item = top.getItem(i);
                if(item != null) {
                    final Adventure a = Adventure.valueOf(i);
                    if(a != null) {
                        final String max = Integer.toString(a.getMaxPlayers()), online = Integer.toString(a.getPlayers().size());
                        itemMeta = item.getItemMeta(); lore.clear();
                        for(String s : itemMeta.getLore()) {
                            lore.add(s.replace("{ONLINE}", online).replace("{MAX}", max));
                        }
                        itemMeta.setLore(lore); lore.clear();
                        item.setItemMeta(itemMeta);
                        final AdventureMap map = a.getRequiredMap();
                        final String mapFoundIn = map != null ? map.getFoundIn() : null;
                        if(mapFoundIn != null && !allowed.contains(a)) {
                            final ItemStack s = mapRequired.clone();
                            itemMeta = s.getItemMeta(); lore.clear();
                            itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{NAME}", item.getItemMeta().getDisplayName()));
                            lore.addAll(item.getItemMeta().getLore());
                            for(String l : itemMeta.getLore()) {
                                lore.add(l.replace("{REQ_ADVENTURE}", mapFoundIn));
                            }
                            itemMeta.setLore(lore); lore.clear();
                            item = s;
                            item.setItemMeta(itemMeta);
                        }
                        itemMeta = item.getItemMeta();
                        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        item.setItemMeta(itemMeta);
                        top.setItem(i, item);
                    }
                }
            }
            player.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void blockBreakEvent(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if(worlds.contains(player.getWorld())) {
            event.setCancelled(true);
            player.updateInventory();
            sendStringListMessage(player, config.getStringList("messages.cannot break blocks"), null);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void blockPlaceEvent(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        if(worlds.contains(player.getWorld())) {
            event.setCancelled(true);
            player.updateInventory();
            sendStringListMessage(player, config.getStringList("messages.cannot place blocks"), null);
        }
    }

    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack i = event.getItem();
        if(i != null) {
            final Adventure a = Adventure.valueOfMap(i);
            if(a != null) {
                final Player player = event.getPlayer();
                event.setCancelled(true);
                player.updateInventory();

                final RSPlayer pdata = RSPlayer.get(player.getUniqueId());
                final List<Adventure> allowed = pdata.getAllowedAdventures();
                if(!allowed.contains(a)) {
                    allowed.add(a);
                    removeItem(player, i, 1);
                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("{ADVENTURE}", a.getName());
                    sendStringListMessage(player, config.getStringList("messages.unlocked access"), replacements);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final String t = event.getView().getTitle();
        if(t.equals(gui.getTitle())) {
            event.setCancelled(true);
            player.updateInventory();
            final ItemStack c = event.getCurrentItem();
            final int r = event.getRawSlot();
            if(r < 0 || r >= player.getOpenInventory().getTopInventory().getSize() || c == null || c.getType().equals(Material.AIR)) return;
            final Adventure a = Adventure.valueOf(r);
            if(a != null) {
                final RSPlayer pdata = RSPlayer.get(player.getUniqueId());
                if(a.getRequiredMap() != null && !pdata.getAllowedAdventures().contains(a)) {
                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("{ADVENTURE}", a.getName());
                    sendStringListMessage(player, config.getStringList("messages.need map to travel"), replacements);
                } else {
                    final ItemStack[] inv = player.getInventory().getContents();
                    a.join(player);
                }
                player.updateInventory();
            }
        }
    }
}
