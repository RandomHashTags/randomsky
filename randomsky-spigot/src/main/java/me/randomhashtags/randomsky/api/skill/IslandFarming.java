package me.randomhashtags.randomsky.api.skill;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randomsky.addon.FarmingRecipe;
import me.randomhashtags.randomsky.addon.active.ActiveIslandSkill;
import me.randomhashtags.randomsky.addon.file.FileFarmingLimitIncreaser;
import me.randomhashtags.randomsky.addon.file.FileFarmingRecipe;
import me.randomhashtags.randomsky.addon.file.FileFarmingSkill;
import me.randomhashtags.randomsky.addon.island.Island;
import me.randomhashtags.randomsky.addon.island.skill.FarmingSkill;
import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.api.IslandAddon;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSStorage;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.io.File.separator;

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

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();

        final String farmingFolder = dataFolder + separator + "island" + separator + "skills" + separator + "farming";
        save(farmingFolder, "_settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(farmingFolder, "_settings.yml"));
        settings = YamlConfiguration.loadConfiguration(new File(dataFolder + separator + "island", "_settings.yml"));

        plantGrownSentWhenEndsIn = new ArrayList<>();
        viewing = new ArrayList<>();

        for(String s : config.getString("messages.plant grown sent when ends in").split(";")) {
            plantGrownSentWhenEndsIn.add(Integer.parseInt(s));
        }

        final List<String> settingsFormat = colorizeListString(config.getStringList("info.settings.format"));
        completedStatus = colorizeListString(config.getStringList("info.settings.completed.status"));
        lockedStatus = colorizeListString(config.getStringList("info.settings.locked.status"));
        inprogressStatus = colorizeListString(config.getStringList("info.settings.in progress.status"));
        farmingRecipe = colorizeListString(config.getStringList("info.settings.farming recipe"));
        needsRecipe = colorize(config.getString("info.settings.needs recipe"));
        hasRecipe = colorize(config.getString("info.settings.has recipe"));

        completedPrefix = colorize(config.getString("info.settings.completed.prefix"));
        lockedPrefix = colorize(config.getString("info.settings.locked.prefix"));
        inprogressPrefix = colorize(config.getString("info.settings.in progress.prefix"));

        for(File f : new File(farmingFolder + separator + "recipes").listFiles()) {
            new FileFarmingRecipe(f);
        }

        final List<String> defaultRecipes = config.getStringList("default recipes");
        for(String s : defaultRecipes) {
            final Identifiable i = RSStorage.get(Feature.FARMING_RECIPE, s);
            if(i != null) {
                FarmingRecipe.defaults.add((FarmingRecipe) i);
            }
        }

        info = new UInventory(null, config.getInt("info.size"), colorize(config.getString("info.title")));
        final Inventory ii = info.getInventory();

        for(File f : new File(farmingFolder).listFiles()) {
            if(!f.isDirectory() && !f.getAbsoluteFile().getName().equals("_settings.yml")) {
                final FarmingSkill skill = new FileFarmingSkill(f);
                ii.setItem(skill.getSlot(), skill.getItem());
            }
        }
        for(File f : new File(farmingFolder + separator + "limit increasers").listFiles()) {
            new FileFarmingLimitIncreaser(f);
        }

        sendConsoleMessage("&6[RandomSky] &aLoaded " + RSStorage.getAll(Feature.FARMING_RECIPE).size() + " Farming Recipes and " + RSStorage.getAll(Feature.FARMING_LIMIT_INCREASE) + " Farming Limit Increasers &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        RSStorage.unregisterAll(Feature.FARMING_RECIPE, Feature.FARMING_LIMIT_INCREASE);
    }

    public void viewFarming(@NotNull Player player) {
        final Island island = Island.players.getOrDefault(player.getUniqueId(), null);
        if(island == null) {
            sendStringListMessage(player, settings.getStringList("messages.need island"), null);
        } else if(hasPermission(player, "RandomSky.island.farming", true)) {
            player.closeInventory();
            final int size = info.getSize();
            player.openInventory(Bukkit.createInventory(player, size, info.getTitle().replace("{PLAYER}", player.getName())));
            viewing.add(player);
            final Inventory top = player.getOpenInventory().getTopInventory();
            final HashMap<FarmingRecipe, BigDecimal> cropsGrown = island.getCropsGrown();
            final List<FarmingRecipe> allowedCrops = island.getAllowedCrops(), defaults = FarmingRecipe.defaults;
            top.setContents(info.getInventory().getContents());
            for(int i = 0; i < size; i++) {
                item = top.getItem(i);
                final FarmingSkill is = FarmingSkill.slots.getOrDefault(i, null);
                if(is != null) {
                    final FarmingSkill r = is.required;
                    final FarmingRecipe req = is.getRequiredRecipe(), previousRecipe = r != null ? r.requiredRecipe : null;
                    final boolean isDefault = defaults.contains(req), isUnlocked = r == null || allowedCrops.contains(req);
                    final double c = is.getCompletion().doubleValue(), p = previousRecipe != null ? cropsGrown.getOrDefault(previousRecipe, BigDecimal.ZERO).doubleValue() : 0;
                    double percent = (p/c)*100;
                    if(percent > 100) percent = 100;

                    itemMeta = item.getItemMeta(); lore.clear();
                    itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    final String NAME = ChatColor.stripColor(itemMeta.getDisplayName()), completion = formatDouble(c), progress = formatDouble(p == -1 ? 0.00 : Math.min(p, c)), T = r != null ? r.type : is.type, P = percent > 0.00 ? formatDouble(round(percent, 2)) : "0";
                    itemMeta.setDisplayName((r == null || percent >= 100.00 ? completedPrefix : isUnlocked && p != -1 ? inprogressPrefix : lockedPrefix) + NAME);
                    for(String s : itemMeta.getLore()) {
                        if(s.equals("{STATUS}")) {
                            if(r != null) {
                                lore.addAll(!isUnlocked || p == -1 ? lockedStatus : percent >= 100.00 ? completedStatus : inprogressStatus);
                            }
                        } else if(s.equals("{FARMING_RECIPE}")) {
                            if(req != null && !isDefault) {
                                final String R = isUnlocked ? hasRecipe : needsRecipe, reqN = req.getName();
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
                    if(r == null || p >= 100) {
                        item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
                    }
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
        if(b.getWorld().getName().equals(islandWorld)) {
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
        final List<FarmingRecipe> allowedCrops = island.getAllowedCrops();
        final HashMap<FarmingRecipe, BigDecimal> cropsGrown = island.getCropsGrown();
        if(allowedCrops.contains(crop) && cropsGrown.get(crop) != null) {
            final ActiveIslandSkill skill = island.farmingSkill;
            final FarmingSkill current = (FarmingSkill) skill.skill, nextSkill = FarmingSkill.valueOf(current);
            final FarmingRecipe ne = nextSkill.getRequiredRecipe();
            if(ne != null && !allowedCrops.contains(ne)) return;
            final BigDecimal a = island.getCropsGrown().getOrDefault(crop, BigDecimal.ZERO).add(BigDecimal.ONE);
            cropsGrown.put(crop, a);
            skill.progress++;
            final double p = skill.progress, c = nextSkill.completion, percent = round((p/c)*100, 2);
            if(percent >= 101) return;
            final List<Player> online = island.getOnlineMembers();
            final String d = formatDouble(p);
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{PROGRESS}", d);
            replacements.put("{TYPE}", crop.getName());
            replacements.put("{MAX}", formatDouble(c));
            replacements.put("{PROGRESS%}", formatDouble(percent));
            if(percent == 100) {
                final FarmingSkill n = FarmingSkill.levels.getOrDefault(nextSkill.level, null);
                if(n == null) return;
                replacements.put("{LEVEL}", Integer.toString(nextSkill.level));
                replacements.put("{TYPE}", n.getType());
                island.farmingSkill = new ActiveIslandSkill(n, n.level, 0);
                cropsGrown.put(ne, BigDecimal.ZERO);
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
                    final List<FarmingRecipe> allowedCrops = is.getAllowedCrops();
                    if(!allowedCrops.contains(f)) {
                        final HashMap<String, String> replacements = new HashMap<>();
                        replacements.put("{PLAYER}", player.getName());
                        replacements.put("{IS_CREATOR}", Bukkit.getOfflinePlayer(is.getCreator()).getName());
                        replacements.put("{TYPE}", f.getName());
                        removeItem(player, i, 1);
                        allowedCrops.add(f);
                        is.getCropsGrown().put(f, null);
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
            if(!is.getAllowedCrops().contains(f) || is.getCropsGrown().getOrDefault(f, null) == null) {
                event.setCancelled(true);
                player.updateInventory();
            }
        }
    }
}
