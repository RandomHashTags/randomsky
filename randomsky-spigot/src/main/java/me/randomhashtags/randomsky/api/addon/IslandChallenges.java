package me.randomhashtags.randomsky.api.addon;

import me.randomhashtags.randomsky.addon.active.ActiveIslandChallenge;
import me.randomhashtags.randomsky.addon.island.Island;
import me.randomhashtags.randomsky.addon.island.IslandChallenge;
import me.randomhashtags.randomsky.api.IslandAddon;
import me.randomhashtags.randomsky.util.RSPlayer;
import me.randomhashtags.randomsky.util.universal.UInventory;
import me.randomhashtags.randomsky.util.universal.UMaterial;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class IslandChallenges extends IslandAddon implements Listener, CommandExecutor {
    private static IslandChallenges instance;
    public static IslandChallenges getIslandChallenges() {
        if(instance == null) instance = new IslandChallenges();
        return instance;
    }

    public YamlConfiguration config, settings;

    private UInventory gui;
    private ItemStack completed, progress, locked;
    private List<String> claimed, claim;
    private String nextChallengeObjectivePrefix;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        if(player != null) viewChallenges(player);
        return true;
    }

    public void load() {
        final long a = System.currentTimeMillis();
        save(null, "island challenges.yml");
        config = YamlConfiguration.loadConfiguration(new File(randomsky.getDataFolder(), "island challenges.yml"));
        settings = YamlConfiguration.loadConfiguration(new File(randomsky.getDataFolder(), "island settings.yml"));

        claim = colorizeListString(config.getStringList("challenges.settings.completed.claim"));
        claimed = colorizeListString(config.getStringList("challenges.settings.completed.claimed"));
        completed = d(config, "challenges.settings.completed");
        progress = d(config, "challenges.settings.progress");
        locked = d(config, "challenges.settings.locked");

        nextChallengeObjectivePrefix = ChatColor.translateAlternateColorCodes('&', config.getString("messages.next challenge objective prefix"));

        gui = new UInventory(null, config.getInt("gui.size"), ChatColor.translateAlternateColorCodes('&', config.getString("gui.title")));
        final Inventory gi = gui.getInventory();
        int loaded = 0;
        for(String s : config.getConfigurationSection("challenges").getKeys(false)) {
            if(!s.equals("settings")) {
                final String p = "challenges." + s + ".";
                final int slot = config.getInt(p + "slot");
                final IslandChallenge c = new IslandChallenge(s, slot, ChatColor.translateAlternateColorCodes('&', config.getString(p + "name")), config.getDouble(p + "completion"), config.getStringList(p + "objective"), config.getStringList(p + "rewards"), config.getStringList(p + "attributes"));
                loaded++;
                item = locked.clone(); itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{NAME}", c.name));
                item.setItemMeta(itemMeta);
                gi.setItem(slot, item);
            }
        }
        sendConsoleMessage("&6{RandomSky] &aLoaded " + loaded + " Island Challenges &e(took " + (System.currentTimeMillis()-a) + "ms)");
    }

    public void unload() {
        IslandChallenge.paths.clear();
    }

    public void viewChallenges(Player player) {
        if(hasPermission(player, "RandomSky.island.challenges", true)) {
            final Island is = Island.players.getOrDefault(player.getUniqueId(), null);
            if(is == null) {
                sendStringListMessage(player, settings.getStringList("messages.need island"), null);
            } else {
                final ActiveIslandChallenge challenge = is.challenge;
                final IslandChallenge type = challenge.type;
                final HashMap<String, Boolean> completedChallenges = is.completedChallenges;
                final double p = challenge.progress;
                final String P = formatDouble(p), percent = formatDouble(round((p/challenge.type.completion)*100, 2));
                player.closeInventory();
                final int size = gui.getSize();
                player.openInventory(Bukkit.createInventory(player, size, gui.getTitle()));
                final Inventory top = player.getOpenInventory().getTopInventory();
                top.setContents(gui.getInventory().getContents());
                for(int i = 0; i < size; i++) {
                    final IslandChallenge c = IslandChallenge.slots.getOrDefault(i, null);
                    if(c != null) {
                        top.setItem(i, getStatus(top, i, type, c, completedChallenges, P, percent));
                    }
                }
                player.updateInventory();
            }
        }
    }
    private ItemStack getStatus(Inventory top, int i, IslandChallenge type, IslandChallenge c, HashMap<String, Boolean> completedChallenges, String P, String percent) {
        final String a = c.path;
        final boolean isCompleted = completedChallenges.containsKey(a), isClaimed = isCompleted ? completedChallenges.get(a) : false;
        final double completion = c.completion;
        final String N = c.name, C = formatDouble(completion);
        final List<String> R = c.rewards, obj = c.objective;
        if(isCompleted) {
            item = completed.clone(); itemMeta = item.getItemMeta(); lore.clear();
            itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{NAME}", N));
            for(String s : itemMeta.getLore()) {
                if(s.equals("{STATUS}")) {
                    lore.addAll(isClaimed ? claimed : claim);
                } else {
                    lore.add(s);
                }
            }
            itemMeta.setLore(lore); lore.clear();
            item.setItemMeta(itemMeta);
        } else if(type == c) {
            item = progress.clone(); itemMeta = item.getItemMeta(); lore.clear();
            itemMeta.setDisplayName(item.getItemMeta().getDisplayName().replace("{NAME}", N));
            for(String s : itemMeta.getLore()) {
                lore.add(s.replace("{PROGRESS}", P).replace("{COMPLETION}", C).replace("{PERCENT}", percent));
            }
            itemMeta.setLore(lore); lore.clear();
            item.setItemMeta(itemMeta);
        } else {
            item = top.getItem(i);
        }
        itemMeta = item.getItemMeta(); lore.clear();
        if(isCompleted && !isClaimed) itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        for(String s : itemMeta.getLore()) {
            if(s.contains("{OBJ}")) {
                for(String o : obj) {
                    String ob = ChatColor.translateAlternateColorCodes('&', o);
                    ob = isClaimed ? ChatColor.stripColor(ob) : ob;
                    lore.add(s.replace("{OBJ}", ob));
                }
            } else if(s.equals("{REWARDS}")) {
                for(String m : R) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', m.split(";")[1]));
                }
            } else {
                lore.add(s);
            }
        }
        itemMeta.setLore(lore); lore.clear();
        item.setItemMeta(itemMeta);
        if(isCompleted && !isClaimed) item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        return item;
    }

    public void increaseChallenge(Event event, Player player, Island island, double increment) {
        final IslandChallengeProgressEvent e = new IslandChallengeProgressEvent(event, player, island, increment);
        pluginmanager.callEvent(e);
        if(!e.isCancelled()) {
            final ActiveIslandChallenge a = island.challenge;
            final IslandChallenge t = a.type;
            a.progress += increment;
            if(a.progress/t.completion >= 1.00) {
                island.completedChallenges.put(t.path, false);
                final IslandChallenge next = IslandChallenge.getNextLevel(t);
                island.challenge = next == null ? null : new ActiveIslandChallenge(next, 0.00);

                final List<Player> on = island.getOnlineMembers();
                final String type = t.name, nt = next != null ? next.name : "";
                final List<String> obj = next != null ? next.objective : null;
                for(String s : config.getStringList("messages.complete")) {
                    if(s.equals("{NEXT_CHALLENGE}")) {
                        if(next != null) {
                            for(String o : config.getStringList("messages.next challenge")) {
                                if(o.contains("{OBJ}")) {
                                    o = ChatColor.translateAlternateColorCodes('&', o.replace("{OBJ}", nextChallengeObjectivePrefix+ChatColor.stripColor(obj.get(0))));
                                    for(Player p : on) p.sendMessage(o);
                                    for(int i = 1; i < obj.size(); i++) {
                                        for(Player p : on) p.sendMessage(ChatColor.translateAlternateColorCodes('&', nextChallengeObjectivePrefix+ChatColor.stripColor(obj.get(i))));
                                    }
                                } else {
                                    o = o.replace("{NEXT_CHALLENGE}", nt);
                                    for(Player p : on) p.sendMessage(ChatColor.translateAlternateColorCodes('&', o));
                                }
                            }
                        }
                    } else {
                        s = s.replace("{CHALLENGE}", type);
                        for(Player p : on) p.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
                    }
                }
            }
        }
    }

    public void giveRewards(Player player, IslandChallenge c) {
        if(player != null && c != null) {
            for(String s : c.rewards) {
                giveItem(player, d(null, s.split(";")[0]));
            }
        }
    }


    private void doAttribute(BlockPlaceEvent event, String attribute, UMaterial block, Player player, Island island) {
        if(!attribute.isEmpty()) {
            final String a = attribute.toLowerCase(), u = block.name().toLowerCase();
            if(a.equals("block=" + u)
                    || a.startsWith("blockis=") && u.endsWith(a.split("=")[1].toLowerCase())) {
                increaseChallenge(event, player, island, 1);
            }
        }
    }
    private void doAttribute(BlockBreakEvent event, String attribute, UMaterial block, Block b, Player player, Island island) {
        if(!attribute.isEmpty()) {
            final String a = attribute.toLowerCase(), u = block.name().toLowerCase();
            if(a.equals("block=" + u)
                    || a.startsWith("blockis=") && u.endsWith(a.split("=")[1].toLowerCase())
            ) {
                increaseChallenge(event, player, island, 1);
            } else if(a.startsWith("crop=")) {
                final UMaterial um = IslandFarming.getIslandFarming().fromBlock(b.getState().getData().toString());
                if(um != null && um.name().toLowerCase().equals(a.split("=")[1].toLowerCase())) {
                    increaseChallenge(event, player, island, 1);
                }
            }
        }
    }
    private void doAttribute(EntityDeathEvent event, String attribute, Player player, Island island) {
        if(!attribute.isEmpty()) {
            final String a = attribute.toLowerCase(), e = event.getEntity().getType().name().toLowerCase();
            if(a.equals("type=" + e)) {
                increaseChallenge(event, player, island, 1);
            }
        }
    }
    private void doAttribute(HarvestResourceNodeEvent event, String attribute, Player player, Island island) {
        if(!attribute.isEmpty()) {
            final String a = attribute.toLowerCase(), p = event.node.type.path;
            if(a.equals("path=" + p)) {
                increaseChallenge(event, player, island, 1);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void blockPlaceEvent(BlockPlaceEvent event) {
        final Block b = event.getBlockPlaced();
        if(b.getWorld().getName().equals(islandWorld)) {
            final Player player = event.getPlayer();
            final Island is = Island.valueOf(b.getLocation());
            if(is != null) {
                final ActiveIslandChallenge a = is.challenge;
                if(a != null) {
                    final UMaterial block = UMaterial.match(event.getItemInHand());
                    for(String s : a.type.attributes) {
                        final String eventAttribute = s.split(";")[0].toLowerCase();
                        final int o = eventAttribute.length();
                        if(eventAttribute.equals("blockplace")) {
                            final String attribute = s.substring(o+1);
                            for(String at : attribute.split(";")) {
                                doAttribute(event, at, block, player, is);
                            }
                        }
                    }
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerIslandBreakBlockEvent(PlayerIslandBreakBlockEvent event) {
        final Player player = event.player;
        final Island is = event.island;
        final BlockBreakEvent e = event.breakEvent;
        final Block b = e.getBlock();
        final Location l = b.getLocation();
        final ActiveIslandChallenge a = is.challenge;
        if(a != null) {
            final UMaterial block = UMaterial.getItem(b);
            for(String s : a.type.attributes) {
                final String eventAttribute = s.split(";")[0].toLowerCase();
                if(eventAttribute.equals("blockbreak")) {
                    String attribute = s.toLowerCase().substring(eventAttribute.length()+1);
                    if(attribute.contains("isgenerated;")) {
                        if(generated.contains(l)) {
                            attribute = attribute.split("isgenerated;")[1];
                            doBlockBreak(e, attribute, block, b, player, is);
                        }
                    } else if(attribute.contains("iscrop;")) {
                        final String c = b.getState().getData().toString();
                        if(c.contains("CROP")) {
                            attribute = attribute.split("iscrop;")[1];
                            doBlockBreak(e, attribute, block, b, player, is);
                        }
                    } else {
                        doBlockBreak(e, attribute, block, b, player, is);
                    }
                }
            }
        }
    }
    private void doBlockBreak(BlockBreakEvent event, String attribute, UMaterial block, Block b, Player player, Island is) {
        for(String at : attribute.split(";")) doAttribute(event, at, block, b, player, is);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void harvestResourceNodeEvent(HarvestResourceNodeEvent event) {
        final Island is = event.island;
        final ActiveIslandChallenge a = is.challenge;
        if(a != null) {
            final Player player = event.player;
            for(String s : a.type.attributes) {
                final String eventAttribute = s.split(";")[0].toLowerCase(), attribute = s.substring(eventAttribute.length()+1);
                if(eventAttribute.equals("harvestresourcenode")) {
                    for(String at : attribute.split(";")) {
                        doAttribute(event, at, player, is);
                    }
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityDeathEvent(EntityDeathEvent event) {
        final Player player = event.getEntity().getKiller();
        if(player != null) {
            final Island is = RSPlayer.get(player.getUniqueId()).getIsland();
            if(is != null) {
                final ActiveIslandChallenge a = is.challenge;
                if(a != null) {
                    for(String s : a.type.attributes) {
                        final String eventAttribute = s.split(";")[0].toLowerCase(), attribute = s.substring(eventAttribute.length()+1);
                        if(eventAttribute.equals("slainmob")) {
                            for(String at : attribute.split(";")) {
                                doAttribute(event, at, player, is);
                            }
                        }
                    }
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        if(event.getView().getTitle().equals(gui.getTitle())) {
            final Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            player.updateInventory();
            final ItemStack c = event.getCurrentItem();
            final int r = event.getRawSlot();
            final Inventory top = player.getOpenInventory().getTopInventory();
            if(r < 0 || r >= top.getSize() || c == null || c.getType().equals(Material.AIR)) return;

            final Island island = RSPlayer.get(player.getUniqueId()).getIsland();
            final ActiveIslandChallenge a = island.challenge;
            if(a != null) {
                final IslandChallenge i = IslandChallenge.slots.getOrDefault(r, null), type = a.type;
                if(i != null) {
                    final String t = i.path;
                    final HashMap<String, Boolean> completed = island.completedChallenges;
                    if(a.type == i) {
                        sendStringListMessage(player, config.getStringList("messages.must complete before claiming rewards"), null);
                    } else if(completed.containsKey(t)) {
                        if(!completed.get(t)) {
                            giveRewards(player, i);
                            completed.put(t, true);
                            player.updateInventory();
                            top.setItem(r, getStatus(top, r, type, i, completed, null, null));

                            final String N = i.name;
                            for(String s : config.getStringList("messages.claimed")) {
                                if(s.equals("{REWARDS}")) {
                                    for(String p : i.rewards) {
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', p.split(";")[1]));
                                    }
                                } else {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', s.replace("{CHALLENGE}", N)));
                                }
                            }
                        } else {
                            sendStringListMessage(player, config.getStringList("messages.already claimed"), null);
                        }
                    } else {
                        sendStringListMessage(player, config.getStringList("messages.must complete challenge requirements"), null);
                    }
                }
            }
        }
    }
}