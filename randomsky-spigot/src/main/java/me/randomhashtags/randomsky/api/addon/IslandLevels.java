package me.randomhashtags.randomsky.api.addon;

import me.randomhashtags.randomsky.addon.island.Island;
import me.randomhashtags.randomsky.addon.island.IslandLevel;
import me.randomhashtags.randomsky.api.IslandAddon;
import me.randomhashtags.randomsky.util.universal.UInventory;
import me.randomhashtags.randomsky.util.universal.UMaterial;
import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class IslandLevels extends IslandAddon implements CommandExecutor {
    private static IslandLevels instance;
    public static IslandLevels getIslandLevels() {
        if(instance == null) instance = new IslandLevels();
        return instance;
    }

    public YamlConfiguration levelsConfig;

    private UInventory gui;
    private ItemStack background, locked, unlocked;
    private List<String> format, currentLevel, clickToLevelUp, cannotAffordToLevelUp, requiresLevel;
    private HashMap<String, String> unlockableBlocks;
    private String spawnerLimit, islandRadius, allowPlacement, maxMembers;

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "island levels.yml");
        levelsConfig = YamlConfiguration.loadConfiguration(new File(rsd, "island levels.yml"));

        unlockableBlocks = new HashMap<>();

        gui = new UInventory(null, levelsConfig.getInt("settings.size"), ChatColor.translateAlternateColorCodes('&', levelsConfig.getString("settings.title")));
        final Inventory gi = gui.getInventory();
        background = d(levelsConfig, "settings.background");
        format = colorizeListString(levelsConfig.getStringList("settings.format"));
        currentLevel = colorizeListString(levelsConfig.getStringList("settings.current level"));
        clickToLevelUp = colorizeListString(levelsConfig.getStringList("settings.click to level up"));
        cannotAffordToLevelUp = colorizeListString(levelsConfig.getStringList("settings.cannot afford level up"));
        requiresLevel = colorizeListString(levelsConfig.getStringList("settings.requires level"));
        for(String s : levelsConfig.getStringList("levels.unlockable blocks")) {
            unlockableBlocks.put(s.toLowerCase().split(";")[0], s.split(";")[1]);
        }

        spawnerLimit = ChatColor.translateAlternateColorCodes('&', levelsConfig.getString("settings.lore formats.spawner limit"));
        islandRadius = ChatColor.translateAlternateColorCodes('&', levelsConfig.getString("settings.lore formats.island radius"));
        allowPlacement = ChatColor.translateAlternateColorCodes('&', levelsConfig.getString("settings.lore formats.allow placement"));
        maxMembers = ChatColor.translateAlternateColorCodes('&', levelsConfig.getString("settings.lore formats.max members"));

        locked = d(levelsConfig, "settings.locked");
        unlocked = d(levelsConfig, "settings.unlocked");
        int prev = 0;

        for(String s : levelsConfig.getConfigurationSection("levels").getKeys(false)) {
            if(!s.equals("unlockable blocks")) {
                final HashMap<Integer, IslandLevel> levels = IslandLevel.levels;
                final String p = "levels." + s + ".";
                final List<String> cost = levelsConfig.getStringList(p + "cost");
                long cosT = 0;
                for(String c : cost) {
                    if(c.startsWith("$")) {
                        cosT = Long.parseLong(c.split("\\$")[1]);
                    }
                }
                final String C = String.format("%,d", cosT);
                final int slot = levelsConfig.getInt(p + "slot");
                final IslandLevel a = levels != null ? levels.getOrDefault(prev, null) : null;
                new IslandLevel(s, slot, prev+1, cost, levelsConfig.getStringList(p + "rewards"), a);
                prev++;
                item = locked.clone(); itemMeta = item.getItemMeta(); lore.clear();
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{LEVEL}", Integer.toString(prev)));
                for(String l : format) {
                    lore.add(l.replace("{COST}", C));
                }
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
                gi.setItem(slot, item);
            }
        }
        for(int i = 0; i < gui.getSize(); i++) {
            if(gi.getItem(i) == null) {
                gi.setItem(i, background);
            }
        }
        sendConsoleMessage("&6[RandomSky] &aLoaded " + levels.size() + " Island Levels &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }

    public void unload() {
        IslandLevel.deleteAll();
    }


    public void viewLevels(Player player) {
        if(hasPermission(player, "RandomSky.island.levels", true)) {
            final Island is = Island.players.getOrDefault(player.getUniqueId(), null);
            if(is != null) {
                final IslandLevel L = is.level;
                final int size = gui.getSize(), isLevel = L.level;
                final double bal = eco.getBalance(player);
                player.openInventory(Bukkit.createInventory(player, size, gui.getTitle()));
                final Inventory top = player.getOpenInventory().getTopInventory();
                top.setContents(gui.getInventory().getContents());
                final HashMap<Integer, IslandLevel> slots = IslandLevel.slots;
                for(int i = 0; i < size; i++) {
                    final IslandLevel l = slots.getOrDefault(i, null);
                    if(l != null) {
                        top.setItem(i, getStatus(bal, i, l, isLevel));
                    }
                }
                player.updateInventory();
            } else {
                sendStringListMessage(player, config.getStringList("messages.need island"), null);
            }
        }
    }
    private long get$Cost(IslandLevel level) {
        for(String s : level.cost) {
            if(s.startsWith("$")) {
                return Long.parseLong(s.split("\\$")[1]);
            }
        }
        return -1;
    }
    private ItemStack getStatus(double bal, int slot, IslandLevel level, int isLevel) {
        final int lvl = level.level;
        item = gui.getInventory().getItem(slot).clone();
        itemMeta = item.getItemMeta(); lore.clear();
        final List<String> L = itemMeta.getLore(), status, rewards = level.rewards;
        List<String> lockedStatus = null;
        if(lvl == isLevel) {
            item = unlocked.clone();
            itemMeta = item.getItemMeta();
            status = currentLevel;
        } else if(lvl < isLevel) {
            item = unlocked.clone();
            itemMeta = item.getItemMeta();
            status = itemMeta.getLore();
        } else {
            status = locked.getItemMeta().getLore();
            lockedStatus = lvl == isLevel+1 ? bal >= get$Cost(level) ? clickToLevelUp : cannotAffordToLevelUp : requiresLevel;
        }
        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{LEVEL}", Integer.toString(lvl)));
        for(String s : L) {
            if(s.equals("{REWARDS}")) {
                for(String r : rewards) {
                    final String R = r.toLowerCase();
                    final double a = R.contains("=") ? Double.parseDouble(R.split("=")[1]) : 0.00;
                    final String f = formatDouble(a);
                    if(R.startsWith("spawnerlimit=")) {
                        lore.add(spawnerLimit.replace("{LIMIT}", f));
                    } else if(R.startsWith("islandradius=")) {
                        lore.add(islandRadius.replace("{RADIUS}", f));
                    } else if(R.startsWith("allowplacement{")) {
                        lore.add(allowPlacement.replace("{BLOCK}", r.split("\\{")[1].split("}")[0]));
                    } else if(R.startsWith("maxmembers=")) {
                        lore.add(maxMembers.replace("{MAX}", f));
                    }
                }
            } else if(s.equals("{STATUS}")) {
                if(lockedStatus == null) {
                    lore.addAll(status);
                } else {
                    for(String k : status) {
                        if(k.equals("{STATUS}")) {
                            for(String l : lockedStatus) {
                                lore.add(l.replace("{LEVEL}", Integer.toString(lvl-1)));
                            }
                        } else {
                            lore.add(k);
                        }
                    }
                }
            } else {
                lore.add(s);
            }
        }
        itemMeta.setLore(lore); lore.clear();
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void islandPlaceBlockEvent(IslandPlaceBlockEvent event) {
        final ItemStack i = event.item;
        final String b = UMaterial.match(i).name().toLowerCase();
        final Island island = event.island;
        final IslandLevel level = island.level;
        if(unlockableBlocks.containsKey(b)) {
            final IslandLevel req = IslandLevel.paths.getOrDefault(unlockableBlocks.get(b), null);
            if(req != null) {
                final int reql = req.level, l = level.level;
                if(l < reql) {
                    event.setCancelled(true);
                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("{REQ_LEVEL}", Integer.toString(reql));
                    replacements.put("{BLOCK}", b.toUpperCase());
                    replacements.put("{LEVEL}", Integer.toString(l));
                    sendStringListMessage(event.player, levelsConfig.getStringList("messages.level too low to place block"), replacements);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        if(event.getView().getTitle().equals(gui.getTitle())) {
            event.setCancelled(true);
            player.updateInventory();
            final ItemStack c = event.getCurrentItem();
            final int r = event.getRawSlot();
            if(r < 0 || r >= top.getSize() || c == null || c.getType().equals(Material.AIR)) return;

            final Island is = Island.players.get(player.getUniqueId());
            final IslandLevel level = IslandLevel.slots.getOrDefault(r, null), current = is.level;
            if(level != null) {
                double bal = eco.getBalance(player);
                final long cost = get$Cost(level);
                final int currentLevel = current.level, targetLevel = level.level;

                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{LEVEL}", Integer.toString(currentLevel));
                replacements.put("{TARGET_LEVEL}", Integer.toString(targetLevel));
                replacements.put("{COST}", formatDouble(cost));
                if(currentLevel >= targetLevel) {
                    sendStringListMessage(player, levelsConfig.getStringList("messages.already unlocked level"), replacements);
                } else if(targetLevel != currentLevel+1) {
                    sendStringListMessage(player, levelsConfig.getStringList("messages.must unlock previous island levels"), replacements);
                } else if(bal < cost) {
                    sendStringListMessage(player, levelsConfig.getStringList("messages.cannot afford level up"), replacements);
                } else {
                    is.setLevel(level);
                    replacements.put("{SIZE}", Integer.toString(is.radius));
                    eco.withdrawPlayer(player, cost);
                    bal -= cost;
                    sendStringListMessage(player, levelsConfig.getStringList("messages.level up"), replacements);
                    final int cu = current.slot;
                    final IslandLevel next = IslandLevel.levels.getOrDefault(targetLevel+1, null);
                    top.setItem(cu, getStatus(bal, cu, current, targetLevel));
                    top.setItem(r, getStatus(bal, level.slot, level, targetLevel));
                    if(next != null) {
                        final int slot = next.slot;
                        top.setItem(slot, getStatus(bal, slot, next, targetLevel));
                    }
                    player.updateInventory();
                }
            }
        }
    }
}
