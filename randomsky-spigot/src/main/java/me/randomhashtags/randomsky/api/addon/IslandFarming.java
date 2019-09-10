package me.randomhashtags.randomsky.api.addon;

import me.randomhashtags.randomsky.addon.FarmingRecipe;
import me.randomhashtags.randomsky.addon.active.ActiveIslandSkill;
import me.randomhashtags.randomsky.addon.island.Island;
import me.randomhashtags.randomsky.api.IslandAddon;
import me.randomhashtags.randomsky.util.universal.UInventory;
import me.randomhashtags.randomsky.util.universal.UMaterial;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IslandFarming extends IslandAddon implements CommandExecutor {
    private static IslandFarming instance;
    public static IslandFarming getIslandFarming() {
        if(instance == null) instance = new IslandFarming();
        return instance;
    }

    public YamlConfiguration config, settings;

    private UInventory info;
    private List<Integer> plantGrownSentWhenEndsIn;
    private List<String> farmingRecipe, completedStatus, lockedStatus, inprogressStatus;
    private List<Player> viewing;
    private String completedPrefix, lockedPrefix, inprogressPrefix, hasRecipe, needsRecipe;

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "island farming.yml");
        config = YamlConfiguration.loadConfiguration(new File(rsd, "island farming.yml"));
        settings = YamlConfiguration.loadConfiguration(new File(rsd, "island settings.yml"));

        plantGrownSentWhenEndsIn = new ArrayList<>();
        viewing = new ArrayList<>();

        for(String s : config.getString("messages.plant grown sent when ends in").split(";")) plantGrownSentWhenEndsIn.add(Integer.parseInt(s));
        final List<String> settingsFormat = colorizeListString(config.getStringList("info.settings.format"));
        completedStatus = colorizeListString(config.getStringList("info.settings.completed.status"));
        lockedStatus = colorizeListString(config.getStringList("info.settings.locked.status"));
        inprogressStatus = colorizeListString(config.getStringList("info.settings.in progress.status"));
        farmingRecipe = colorizeListString(config.getStringList("info.settings.farming recipe"));
        needsRecipe = ChatColor.translateAlternateColorCodes('&', config.getString("info.settings.needs recipe"));
        hasRecipe = ChatColor.translateAlternateColorCodes('&', config.getString("info.settings.has recipe"));

        completedPrefix = ChatColor.translateAlternateColorCodes('&', config.getString("info.settings.completed.prefix"));
        lockedPrefix = ChatColor.translateAlternateColorCodes('&', config.getString("info.settings.locked.prefix"));
        inprogressPrefix = ChatColor.translateAlternateColorCodes('&', config.getString("info.settings.in progress.prefix"));

        int loaded = 0;
        for(String s : config.getConfigurationSection("recipes").getKeys(false)) {
            if(!s.equals("default")) {
                final String p = "recipes." + s + ".";
                final ItemStack i = d(config, "recipes." + s);
                new FarmingRecipe(s, ChatColor.translateAlternateColorCodes('&', config.getString(p + "recipe name")), UMaterial.valueOf(config.getString(p + "unlocks").toUpperCase()), i);
                loaded++;
            }
        }
        final String D = config.getString("recipes.default");
        for(String s : D.split("&&")) FarmingRecipe.defaults.add(FarmingRecipe.paths.get(s));

        info = new UInventory(null, config.getInt("info.size"), ChatColor.translateAlternateColorCodes('&', config.getString("info.title")));
        final Inventory ii = info.getInventory();
        int level = 1;
        for(String s : config.getConfigurationSection("info.settings").getKeys(false)) {
            if(!s.equals("format") && !s.equals("needs recipe") && !s.equals("has recipe") && !s.equals("completed") && !s.equals("locked") && !s.equals("in progress") && !s.equals("farming recipe")) {
                final String p = "info.settings." + s + ".", requiredSkill = config.getString(p + "required skill");
                final int slot = config.getInt(p + "slot"), completionNumber = config.getInt(p + "completion");
                final ItemStack display = d(config, "info.settings." + s);
                new FarmingSkill(s, level, slot, completionNumber, ChatColor.translateAlternateColorCodes('&', config.getString(p + "type")), display, FarmingSkill.valueOf(requiredSkill), FarmingRecipe.valueOf(config.getString(p + "required recipe")));
                item = display.clone(); itemMeta = item.getItemMeta(); lore.clear();
                if(completionNumber == 0 || requiredSkill == null) {
                    if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
                } else {
                    lore.addAll(settingsFormat);
                }
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
                ii.setItem(slot, item);
                level++;
            }
        }
        FarmingSkill.paths.put("default", FarmingSkill.paths.get(D.split("&&")[0]));
        sendConsoleMessage("&6[RandomSky] &aLoaded " + loaded + " Farming Recipes &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        FarmingRecipe.deleteAll();
        FarmingSkill.deleteAll();
        FarmingLimitIncrease.deleteAll();
    }

    public void viewFarming(Player player) {
        final Island island = Island.players.getOrDefault(player.getUniqueId(), null);
        if(island == null) {
            sendStringListMessage(player, settings.getStringList("messages.need island"), null);
        } else if(hasPermission(player, "RandomSky.island.farming", true)) {
            player.closeInventory();
            final int size = info.getSize();
            player.openInventory(Bukkit.createInventory(player, size, info.getTitle().replace("{PLAYER}", player.getName())));
            viewing.add(player);
            final Inventory top = player.getOpenInventory().getTopInventory();
            final HashMap<FarmingRecipe, Integer> cropsGrown = island.cropsGrown;
            final List<FarmingRecipe> allowedCrops = island.allowedCrops, defaults = FarmingRecipe.defaults;
            top.setContents(info.getInventory().getContents());
            for(int i = 0; i < size; i++) {
                item = top.getItem(i);
                final FarmingSkill is = FarmingSkill.slots.getOrDefault(i, null);
                if(is != null) {
                    final FarmingSkill r = is.required;
                    final FarmingRecipe req = is.requiredRecipe, previousRecipe = r != null ? r.requiredRecipe : null;
                    final boolean isDefault = defaults.contains(req), isUnlocked = r == null || allowedCrops.contains(req);
                    final double c = is.completion, p = previousRecipe != null ? cropsGrown.getOrDefault(previousRecipe, -1) : 0;
                    double percent = (p/c)*100;
                    if(percent > 100) percent = 100;

                    itemMeta = item.getItemMeta(); lore.clear();
                    itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    final String NAME = ChatColor.stripColor(itemMeta.getDisplayName()), completion = formatDouble(c), progress = formatDouble(p == -1 ? 0.00 : p > c ? c : p), T = r != null ? r.type : is.type, P = percent > 0.00 ? formatDouble(round(percent, 2)) : "0";
                    itemMeta.setDisplayName((r == null || percent >= 100.00 ? completedPrefix : isUnlocked && p != -1 ? inprogressPrefix : lockedPrefix) + NAME);
                    for(String s : itemMeta.getLore()) {
                        if(s.equals("{STATUS}")) {
                            if(r != null) {
                                lore.addAll(!isUnlocked || p == -1 ? lockedStatus : percent >= 100.00 ? completedStatus : inprogressStatus);
                            }
                        } else if(s.equals("{FARMING_RECIPE}")) {
                            if(req != null && !isDefault) {
                                final String R = isUnlocked ? hasRecipe : needsRecipe, reqN = req.recipeName;
                                for(String f : farmingRecipe) {
                                    lore.add(f.replace("{RECIPE_NAME}", reqN).replace("{HAS_RECIPE}", R));
                                }
                            }
                        } else {
                            lore.add(s.replace("{COMPLETION}", completion).replace("{PROGRESS}", progress).replace("{TYPE}", T).replace("{COMPLETION%}", P));
                        }
                    }
                    itemMeta.setLore(lore); lore.clear();
                    item.setItemMeta(itemMeta);
                    if(r == null || p >= 100) item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
                }
            }
            player.updateInventory();
        }
    }

    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        viewing.remove(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void blockGrowEvent(BlockGrowEvent event) {
        final Block b = event.getBlock();
        if(!event.isCancelled() && b.getWorld().getName().equals(islandWorld)) {
            final Island is = Island.valueOf(b.getLocation());
            if(is != null) {
                final UMaterial seed = fromBlock(event.getNewState().getData().toString());
                final FarmingRecipe f = FarmingRecipe.valueOfSeed(seed);
                if(f != null) {
                    increaseSkill(is, f);
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void blockSpreadEvent(BlockSpreadEvent event) {
        final Location l = event.getBlock().getLocation();
        final World w = l.getWorld();
        if(w.getName().equals(islandWorld)) {
            final Island island = Island.valueOf(l);
            if(island != null) {
                final UMaterial seed = fromBlock(event.getNewState().getData().toString());
                if(seed != null) {
                    final FarmingRecipe f = FarmingRecipe.valueOfSeed(seed);
                    if(f != null) {
                        increaseSkill(island, f);
                    }
                }
            }
        }
    }
    private void increaseSkill(Island island, FarmingRecipe crop) {
        final List<FarmingRecipe> allowedCrops = island.allowedCrops;
        final HashMap<FarmingRecipe, Integer> cropsGrown = island.cropsGrown;
        if(allowedCrops.contains(crop) && cropsGrown.get(crop) != -1) {
            final ActiveIslandSkill skill = island.farmingSkill;
            final FarmingSkill current = (FarmingSkill) skill.skill, nextSkill = FarmingSkill.valueOf(current);
            final FarmingRecipe ne = nextSkill.requiredRecipe;
            if(ne != null && !allowedCrops.contains(ne)) return;
            final int a = island.cropsGrown.getOrDefault(crop, 0)+1;
            cropsGrown.put(crop, a);
            skill.progress++;
            final double p = skill.progress, c = nextSkill.completion, percent = round((p/c)*100, 2);
            if(percent >= 101) return;
            final List<Player> online = island.getOnlineMembers();
            final String d = formatDouble(p);
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{PROGRESS}", d);
            replacements.put("{TYPE}", crop.recipeName);
            replacements.put("{MAX}", formatDouble(c));
            replacements.put("{PROGRESS%}", formatDouble(percent));
            if(percent == 100) {
                final FarmingSkill n = FarmingSkill.levels.getOrDefault(nextSkill.level, null);
                if(n == null) return;
                replacements.put("{LEVEL}", Integer.toString(nextSkill.level));
                replacements.put("{TYPE}", n.type);
                island.farmingSkill = new ActiveIslandSkill(n, n.level, 0);
                cropsGrown.put(ne, 0);
                final List<String> ad = config.getStringList("messages.skill advanced");
                for(Player player : online) {
                    sendStringListMessage(player, ad, replacements);
                }
            } else {
                final String o = Integer.toString((int) p);
                for(int i : plantGrownSentWhenEndsIn) {
                    if(o.endsWith(Integer.toString(i))) {
                        final List<String> m = config.getStringList("messages.plant grown");
                        for(Player player : online) {
                            sendStringListMessage(player, m, replacements);
                        }
                        return;
                    }
                }
            }
        }
    }
    UMaterial fromBlock(String s) {
        if(s.startsWith("RIPE CROPS")) return UMaterial.WHEAT_SEEDS;
        else if(s.startsWith("RIPE BEETROOT_BLOCK")) return UMaterial.BEETROOT_SEEDS;
        else if(s.startsWith("RIPE CARROT")) return UMaterial.CARROT_ITEM;
        else if(s.startsWith("RIPE POTATO")) return UMaterial.POTATO_ITEM;
        else if(s.startsWith("SUGAR_CANE_BLOCK")) return UMaterial.SUGAR_CANE_ITEM;
        else if(s.startsWith("MELON_BLOCK")) return UMaterial.MELON_SEEDS;
        else if(s.startsWith("PUMPKIN") && !s.startsWith("PUMPKIN_")) return UMaterial.PUMPKIN_SEEDS;
        else if(s.contains(" NETHER_WARTS(")) return UMaterial.NETHER_WART;
        else if(s.startsWith("CHORUS_FLOWER")) return UMaterial.CHORUS_FLOWER;
        return null;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        if(viewing.contains(player)) {
            event.setCancelled(true);
            player.updateInventory();
        }
    }
    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack i = event.getItem();
        if(i != null) {
            final FarmingRecipe f = FarmingRecipe.valueOf(i);
            if(f != null) {
                final Player player = event.getPlayer();
                event.setCancelled(true);
                player.updateInventory();
                final Island is = Island.valueOf(player.getLocation());
                if(is == null) {
                    sendStringListMessage(player, settings.getStringList("messages.need island"), null);
                } else {
                    final List<FarmingRecipe> allowedCrops = is.allowedCrops;
                    if(!allowedCrops.contains(f)) {
                        final HashMap<String, String> replacements = new HashMap<>();
                        replacements.put("{PLAYER}", player.getName());
                        replacements.put("{IS_CREATOR}", Bukkit.getOfflinePlayer(is.getCreator()).getName());
                        replacements.put("{TYPE}", f.recipeName);
                        removeItem(player, i, 1);
                        allowedCrops.add(f);
                        is.cropsGrown.put(f, -1);
                        sendStringListMessage(player, config.getStringList("messages.unlocked recipe"), replacements);
                        player.updateInventory();
                    }
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void blockPlaceEvent(BlockPlaceEvent event) {
        final Location bl = event.getBlockPlaced().getLocation();
        final Player player = event.getPlayer();
        final ItemStack i = event.getItemInHand();
        final FarmingRecipe f = FarmingRecipe.valueOfSeed(UMaterial.match(i.getType().name(), i.getData().getData()));
        if(f != null) {
            final Island is = Island.valueOf(bl);
            if(!is.allowedCrops.contains(f) || is.cropsGrown.getOrDefault(f, -1) == -1) {
                event.setCancelled(true);
                player.updateInventory();
            }
        }
    }
}
