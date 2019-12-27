package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.addon.file.FileIslandLevel;
import me.randomhashtags.randomsky.addon.island.Island;
import me.randomhashtags.randomsky.addon.island.IslandLevel;
import me.randomhashtags.randomsky.event.island.IslandPlaceBlockEvent;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSStorage;
import me.randomhashtags.randomsky.universal.UInventory;
import me.randomhashtags.randomsky.universal.UMaterial;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.io.File.separator;

public class IslandLevels extends IslandAddon implements CommandExecutor {
    private static IslandLevels instance;
    public static IslandLevels getIslandLevels() {
        if(instance == null) instance = new IslandLevels();
        return instance;
    }

    public YamlConfiguration config;

    private UInventory gui;
    private ItemStack background, locked, unlocked;
    private List<String> format, currentLevel, clickToLevelUp, cannotAffordToLevelUp, requiresLevel;
    private List<UMaterial> lockedBlocks;
    private String spawnerLimit, islandRadius, allowPlacement, maxMembers;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(DATA_FOLDER + separator + "island" + separator + "levels", "_settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER + separator + "island" + separator + "levels", "_setings.yml"));

        lockedBlocks = new ArrayList<>();

        gui = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        final Inventory gi = gui.getInventory();
        background = d(config, "items.background");
        format = getStringList(config, "items.format");
        currentLevel = getStringList(config, "items.current level");
        clickToLevelUp = getStringList(config, "items.click to level up");
        cannotAffordToLevelUp = getStringList(config, "items.cannot afford level up");
        requiresLevel = getStringList(config, "items.requires level");
        for(String s : getStringList(config, "locked blocks")) {
            lockedBlocks.add(UMaterial.match(s));
        }

        spawnerLimit = colorize(config.getString("settings.lore formats.spawner limit"));
        islandRadius = colorize(config.getString("settings.lore formats.island radius"));
        allowPlacement = colorize(config.getString("settings.lore formats.allow placement"));
        maxMembers = colorize(config.getString("settings.lore formats.max members"));

        locked = d(config, "settings.locked");
        unlocked = d(config, "settings.unlocked");

        for(File f : new File(DATA_FOLDER + separator + "island" + separator + "levels").listFiles()) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                final IslandLevel lvl = new FileIslandLevel(f);
                final int level = lvl.getLevel();
                item = locked.clone(); itemMeta = item.getItemMeta(); lore.clear();
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{LEVEL}", Integer.toString(level)));
                lore.addAll(format);
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
                gi.setItem(lvl.getSlot(), item);
            }
        }

        for(int i = 0; i < gui.getSize(); i++) {
            if(gi.getItem(i) == null) {
                gi.setItem(i, background);
            }
        }
        sendConsoleMessage("&6[RandomSky] &aLoaded " + RSStorage.getAll(Feature.ISLAND_LEVEL).size() + " Island Levels &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }

    public void unload() {
        RSStorage.unregisterAll(Feature.ISLAND_LEVEL);
    }


    public void viewLevels(Player player) {
        if(hasPermission(player, "RandomSky.island.levels", true)) {
            final Island is = Island.players.getOrDefault(player.getUniqueId(), null);
            if(is != null) {
                final IslandLevel L = is.getIslandLevel();
                final int size = gui.getSize(), isLevel = L.getLevel();
                final double bal = ECONOMY.getBalance(player);
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
                sendStringListMessage(player, getStringList(config, "messages.need island"), null);
            }
        }
    }
    private long get$Cost(IslandLevel level) {
        for(String s : level.getCost()) {
            if(s.startsWith("$")) {
                return Long.parseLong(s.split("\\$")[1]);
            }
        }
        return -1;
    }
    private ItemStack getStatus(double bal, int slot, IslandLevel level, int isLevel) {
        final int lvl = level.getLevel();
        item = gui.getInventory().getItem(slot).clone();
        itemMeta = item.getItemMeta(); lore.clear();
        final List<String> L = itemMeta.getLore(), status, rewards = level.getAttributes();
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
        final ItemStack i = event.getItem();
        final String b = UMaterial.match(i).name().toLowerCase();
        final Island island = event.getIsland();
        final IslandLevel level = island.getIslandLevel();
        if(lockedBlocks.containsKey(b)) {
            final IslandLevel req = IslandLevel.paths.getOrDefault(lockedBlocks.get(b), null);
            if(req != null) {
                final int reql = req.getLevel(), l = level.getLevel();
                if(l < reql) {
                    event.setCancelled(true);
                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("{REQ_LEVEL}", Integer.toString(reql));
                    replacements.put("{BLOCK}", b.toUpperCase());
                    replacements.put("{LEVEL}", Integer.toString(l));
                    sendStringListMessage(event.getPlayer(), getStringList(config, "messages.level too low to place block"), replacements);
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
            final IslandLevel level = IslandLevel.slots.getOrDefault(r, null), current = is.getIslandLevel();
            if(level != null) {
                double bal = ECONOMY.getBalance(player);
                final long cost = get$Cost(level);
                final int currentLevel = current.getLevel(), targetLevel = level.getLevel();

                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{LEVEL}", Integer.toString(currentLevel));
                replacements.put("{TARGET_LEVEL}", Integer.toString(targetLevel));
                replacements.put("{COST}", formatDouble(cost));
                if(currentLevel >= targetLevel) {
                    sendStringListMessage(player, getStringList(config, "messages.already unlocked level"), replacements);
                } else if(targetLevel != currentLevel+1) {
                    sendStringListMessage(player, getStringList(config, "messages.must unlock previous island levels"), replacements);
                } else if(bal < cost) {
                    sendStringListMessage(player, getStringList(config, "messages.cannot afford level up"), replacements);
                } else {
                    is.setIslandLevel(level);
                    replacements.put("{SIZE}", Integer.toString(is.radius));
                    ECONOMY.withdrawPlayer(player, cost);
                    bal -= cost;
                    sendStringListMessage(player, getStringList(config, "messages.level up"), replacements);
                    final int cu = current.getSlot();
                    final IslandLevel next = IslandLevel.levels.getOrDefault(targetLevel+1, null);
                    top.setItem(cu, getStatus(bal, cu, current, targetLevel));
                    top.setItem(r, getStatus(bal, level.getSlot(), level, targetLevel));
                    if(next != null) {
                        final int slot = next.getSlot();
                        top.setItem(slot, getStatus(bal, slot, next, targetLevel));
                    }
                    player.updateInventory();
                }
            }
        }
    }
}
