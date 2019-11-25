package me.randomhashtags.randomsky.api.skill;

import me.randomhashtags.randomsky.addon.ResourceNode;
import me.randomhashtags.randomsky.addon.active.ActiveResourceNode;
import me.randomhashtags.randomsky.addon.island.Island;
import me.randomhashtags.randomsky.addon.island.skill.MiningSkill;
import me.randomhashtags.randomsky.api.IslandAddon;
import me.randomhashtags.randomsky.api.Islands;
import me.randomhashtags.randomsky.event.island.IslandPlaceBlockEvent;
import me.randomhashtags.randomsky.util.RSPlayer;
import me.randomhashtags.randomsky.util.ToggleType;
import me.randomhashtags.randomsky.util.universal.UInventory;
import me.randomhashtags.randomsky.util.universal.UMaterial;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class IslandMining extends IslandAddon implements CommandExecutor {
    private static IslandMining instance;
    public static IslandMining getIslandMining() {
        if(instance == null) instance = new IslandMining();
        return instance;
    }

    public YamlConfiguration config;

    static List<Location> generated;
    private BlockFace[] faces;
    private UInventory gui;
    private ItemStack background;
    private String lockedName, unlockedName;

    public static List<String> cosmeticFormat;
    private static List<String> drops, orFormat, respawnRate, lockedAddedLore, unlockedAddedLore;
    private List<String> resourceItemFormat, resourceFormat;
    private List<UMaterial> resources, resourceItems, scraps, cannotBeInstaBroke;
    public HashMap<String, ItemStack> refined, unrefined;

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "island mining.yml");
        config = YamlConfiguration.loadConfiguration(new File(dataFolder, "island mining.yml"));

        generated = new ArrayList<>();
        final List<String> S = otherdata.getStringList("generated");
        if(!S.isEmpty()) {
            for(String s : S) {
                final Location l = toLocation(s);
                if(!l.getWorld().getBlockAt(l).getType().name().contains("AIR")) {
                    generated.add(l);
                }
            }
        }
        faces = new BlockFace[] { BlockFace.SELF, BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
        resources = new ArrayList<>();
        resourceItems = new ArrayList<>();
        scraps = new ArrayList<>();
        refined = new HashMap<>();
        unrefined = new HashMap<>();
        cannotBeInstaBroke = new ArrayList<>();

        for(String s : config.getStringList("cannot be insta broke")) {
            cannotBeInstaBroke.add(UMaterial.match(s.toUpperCase()));
        }

        orFormat = colorizeListString(config.getStringList("gui.settings.or format"));
        respawnRate = colorizeListString(config.getStringList("gui.settings.respawn rate"));
        lockedAddedLore = colorizeListString(config.getStringList("gui.settings.locked.added lore"));
        unlockedAddedLore = colorizeListString(config.getStringList("gui.settings.unlocked.added lore"));

        resourceFormat = colorizeListString(config.getStringList("resources.pre lore"));
        cosmeticFormat = colorizeListString(config.getStringList("cosmetic.pre lore"));
        drops = config.getStringList("drops");

        int bots = 0, resources = 0, resourceItems = 0, fragments = 0, scraps = 0, nodetypes = 0, nodes = 0;
        for(String s : config.getConfigurationSection("bots").getKeys(false)) {
            bots++;
        }
        for(String s : config.getStringList("resources.items")) {
            final UMaterial u = UMaterial.match(s.toUpperCase());
            this.resources.add(u);
            new Resource(ResourceType.RESOURCE, s.toLowerCase(), u.getItemStack());
            resources++;
        }

        resourceItemFormat = colorizeListString(config.getStringList("resource item.pre lore"));
        for(String s : config.getConfigurationSection("resource item").getKeys(false)) {
            if(!s.equals("pre lore")) {
                item = d(config, "resource item." + s); itemMeta = item.getItemMeta(); lore.clear();
                lore.addAll(resourceItemFormat);
                if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
                final UMaterial u = UMaterial.match(item);
                this.resourceItems.add(u);
                new Resource(ResourceType.RESOURCE_ITEM, s.toLowerCase(), item);
                resourceItems++;
            }
        }

        for(String s : config.getConfigurationSection("resource fragments").getKeys(false)) {
            new Resource(ResourceType.FRAGMENT, s, d(config, "resource fragments." + s));
            fragments++;
        }
        final List<String> prelore = config.getStringList("scraps.pre lore");
        for(String s : config.getConfigurationSection("scraps").getKeys(false)) {
            if(!s.equals("pre lore")) {
                item = d(config, "scraps." + s);
                final UMaterial u = UMaterial.match(item);
                this.scraps.add(u);
                itemMeta = item.getItemMeta(); lore.clear();
                for(String l : prelore) lore.add(colorize(l));
                if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
                new Resource(ResourceType.SCRAP, s, item);
                scraps++;
            }
        }
        for(String s : config.getConfigurationSection("nodes.types").getKeys(false)) {
            new ResourceNodeType(s, config.getStringList("nodes.types." + s + ".lore"));
            nodetypes++;
        }
        for(String s : config.getConfigurationSection("nodes").getKeys(false)) {
            if(!s.equals("default") && !s.equals("types")) {
                final String p = "nodes." + s + ".";
                final ResourceNodeType type = ResourceNodeType.types.getOrDefault(config.getString(p + "type"), null);
                final List<String> loot = config.getStringList(p + "loot");
                final UMaterial harvest = UMaterial.valueOf(config.getString(p + "harvest block")), node = UMaterial.valueOf(config.getString(p + "node block"));
                item = d(config, "nodes." + s); itemMeta = item.getItemMeta(); lore.clear();
                for(String l : type.lore) {
                    if(l.equals("{LOOT}")) {
                        for(String L : loot)
                            lore.add(colorize(L.split(";")[1]));
                    } else {
                        lore.add(l);
                    }
                }
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
                new ResourceNode(s, type, IslandLevel.levels.get(config.getInt("nodes." + s + ".required island level")), config.getLong(p + "respawn time"), config.getDouble(p + "value"), harvest, node, colorize(config.getString(p + "node name")), config.getString(p + "node {TYPE}"), config.getString(p + "required node"), config.getInt(p + "completion"), item, loot);
                nodes++;
            }
        }
        ResourceNode.paths.put("default", ResourceNode.paths.get(config.getString("nodes.default")));

        final int size = config.getInt("gui.size");
        final List<String> format = colorizeListString(config.getStringList("gui.settings.format"));
        lockedName = colorize(config.getString("gui.settings.locked.name"));
        unlockedName = colorize(config.getString("gui.settings.unlocked.name"));
        gui = new UInventory(null, size, colorize(config.getString("gui.title")));
        final Inventory gi = gui.getInventory();
        background = d(config, "gui.background");
        final HashMap<String, ResourceNode> paths = ResourceNode.paths;
        for(String s : config.getConfigurationSection("gui").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("background") && !s.equals("settings")) {
                final String p = "gui." + s + ".";
                final int slot = config.getInt(p + "slot");
                final ItemStack display = d(config, "gui." + s);
                item = display.clone(); itemMeta = item.getItemMeta(); lore.clear();
                if(itemMeta.hasLore()) {
                    lore.addAll(itemMeta.getLore());
                }
                lore.addAll(format);
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
                gi.setItem(slot, item);
                new MiningSkill(s, slot, paths.getOrDefault(config.getString(p + "tracks node"), null), display);
            }
        }
        for(int i = 0; i < size; i++) {
            if(gi.getItem(i) == null) {
                gi.setItem(i, background);
            }
        }

        for(String s : config.getConfigurationSection("unrefined").getKeys(false)) {
            unrefined.put(s, d(config, "unrefined." + s));
        }
        for(String s : config.getConfigurationSection("refined").getKeys(false)) {
            refined.put(s, d(config, "refined." + s));
        }

        sendConsoleMessage("&6[RandomSky] &aLoaded Resources: " + bots + " bots, " + resources + " resources, " + resourceItems + " resource items, " + fragments + " fragments, " + scraps + " scraps, " + nodetypes + " node types, " + nodes + " nodes, " + refined.size() + " refined blocks, and " + unrefined.size() + " unrefined blocks &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }

    public void unload() {
        final List<String> s = new ArrayList<>();
        for(Location l : generated) {
            final String e = toString(l);
            if(!s.contains(e)) s.add(e);
        }
        otherdata.set("generated", s);
        saveOtherData();
        generated = null;
        cosmeticFormat = null;
        drops = null;
        orFormat = null;
        respawnRate = null;
        lockedAddedLore = null;
        unlockedAddedLore = null;
        Resource.deleteAll();
        ResourceNodeType.deleteAll();
        ResourceNode.deleteAll();
        MiningSkill.deleteAll();
    }


    public void viewMining(Player player) {
        if(hasPermission(player, "RandomSky.island.mining", true)) {
            final Island island = Island.players.getOrDefault(player.getUniqueId(), null);
            if(island == null) {
                sendStringListMessage(player, Islands.config.getStringList("messages.need island"), null);
            } else {
                final List<ResourceNode> allowed = island.allowedNodes;
                player.closeInventory();
                final int size = gui.getSize();
                player.openInventory(Bukkit.createInventory(player, size, gui.getTitle()));
                final Inventory top = player.getOpenInventory().getTopInventory(), g = gui.getInventory();
                top.setContents(g.getContents());
                final HashMap<Integer, MiningSkill> slots = MiningSkill.slots;
                final HashMap<String, ResourceNode> paths = ResourceNode.paths;
                for(int i : slots.keySet()) {
                    final MiningSkill skill = slots.get(i);
                    final ResourceNode target = skill.tracks, next = ResourceNode.getNextLevel(target);
                    final String rn = target.requiredNode;
                    final ResourceNode required = rn != null ? paths.get(rn.split("\\|\\|")[0]) : null;
                    final int P = island.getMinedResourceNodes(required), p = island.getMinedResourceNodes(target), nc = next != null ? next.completion : 0, c = target.completion, mined = island.getMinedResourceNodes(required);
                    final String NC = formatInt(nc), rr = formatDouble(island.resourceRespawnRate.getOrDefault(target, 1.00)*100), requiredPER = formatDouble(c != 0 ? round(((((double) P)/(double) c))*100, 2) : 0.00);
                    final String PRO = Integer.toString(mined), progress = Integer.toString(p), completion = Integer.toString(c), type = required != null ? required.nodeTYPE : target.nodeTYPE, completionP = Integer.toString((int) (((double) p)/((double) c)*100));
                    item = g.getItem(i).clone(); itemMeta = item.getItemMeta(); lore.clear();
                    final boolean isUnlocked = allowed.contains(target) || rn == null;
                    itemMeta.setDisplayName((isUnlocked ? unlockedName : lockedName).replace("{NAME}", ChatColor.stripColor(itemMeta.getDisplayName())));
                    itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    for(String s : itemMeta.getLore()) {
                        final boolean contains = s.contains("{PROGRESS}") || s.contains("{COMPLETION}") || s.contains("{COMPLETION%}");
                        if(s.equals("{RESPAWN_RATE}")) {
                            for(String r : respawnRate) {
                                lore.add(r.replace("{PROGRESS}", progress).replace("{COMPLETION}", NC).replace("{TYPE}", target.getNodeType()).replace("{RESPAWN%}", rr));
                            }
                        } else if(s.equals("{OR}")) {
                            if(rn != null && rn.contains("||")) {
                                final ResourceNode or = paths.get(rn.split("\\|\\|")[1]);
                                final int orP = island.getMinedResourceNodes(or);
                                final String orT = or.getNodeType(), orProgress = Integer.toString(orP);
                                for(String o : orFormat) {
                                    lore.add(o.replace("{PROGRESS}", orProgress).replace("{TYPE}", orT).replace("{COMPLETION}", completion));
                                }
                            }
                        } else if(rn != null && contains || !contains) {
                            lore.add(s.replace("{PROGRESS}", PRO).replace("{COMPLETION}", completion).replace("{TYPE}", type).replace("{COMPLETION%}", requiredPER));
                        }
                    }
                    itemMeta.setLore(lore); lore.clear();
                    item.setItemMeta(itemMeta);
                    if(isUnlocked) item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
                    top.setItem(i, item);
                }
                player.updateInventory();
            }
        }
    }

    public void breakBlock(Player player, RSPlayer pdata, Block block) {
        final UMaterial u = UMaterial.getItem(block);
        if(cannotBeInstaBroke.contains(u)) return;
        try {
            final String m = u.name();
            final Location bl = block.getLocation();
            final World w = bl.getWorld();
            final ItemStack drop;
            final boolean instantPickup;
            if(generated.contains(bl) && (m.contains("LOG") || m.equals("COBBLESTONE"))) {
                instantPickup = false;
                drop = getDrop(u);
            } else {
                instantPickup = true;
                drop = m.contains("_DOOR") ? UMaterial.match(m + "_ITEM").getItemStack() : u.getItemStack();
                if(drop != null) {
                    itemMeta = drop.getItemMeta(); lore.clear();
                    lore.addAll(cosmeticFormat);
                    itemMeta.setLore(lore); lore.clear();
                    drop.setItemMeta(itemMeta);
                } else return;
            }
            w.getBlockAt(bl).setType(Material.AIR);
            spawnParticle(pdata, w, bl, drop);
            if(instantPickup && pdata.getToggles().get(ToggleType.INSTANT_BLOCK_PICKUP)) {
                giveItem(player, drop);
            } else {
                w.dropItemNaturally(bl.clone().add(0.5, 1, 0.5), drop);
            }
            player.updateInventory();
        } catch (Exception e) {
            final String s = colorize("&6[RandomSky] &cError caught while trying to break &f" + block.getType().name() + ":" + block.getData() + "&c! &e" + VERSION + "&c; &bReport this to RandomHashTags!");
            sendConsoleMessage(s);
            player.sendMessage(s);
        }
    }
    public ItemStack getDrop(UMaterial u) {
        for(String s : drops) {
            final String one = s.split(":")[0].toUpperCase();
            final UMaterial um = UMaterial.valueOf(one);
            if(u == um) {
                return d(null, s.substring(one.length()+1));
            }
        }
        return null;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        if(top.getHolder() == player && event.getView().getTitle().equals(gui.getTitle())) {
            event.setCancelled(true);
            player.updateInventory();
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void blockBurnEvent(BlockBurnEvent event) {
        final Location l = event.getBlock().getLocation();
        if(l.getWorld().getName().equals(islandWorld)) {
            final Island is = Island.valueOf(l);
            if(is != null) {
                final ActiveResourceNode a = is.valueof(l);
                if(a != null) {
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void islandPlaceBlockEvent(IslandPlaceBlockEvent event) {
        final ItemStack i = event.getItem();
        if(i != null && i.hasItemMeta() && i.getItemMeta().hasLore()) {
            final List<String> l = i.getItemMeta().getLore();
            if(l.containsAll(resourceItemFormat) || l.containsAll(resourceFormat)) {
                event.setCancelled(true);
                sendStringListMessage(event.getPlayer(), config.getStringList("messages.cannot place resource items"), null);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void blockFromToEvent(BlockFromToEvent event) {
        final Block block = event.getBlock(), generated = event.getToBlock();
        final Material b = generated.getType();
        if(block.getWorld().getName().equals(islandWorld) && b.name().contains("AIR") && generatesCobble(generated, block)) {
            IslandMining.generated.add(generated.getLocation());
        }
    }
    private boolean generatesCobble(Block generated, Block b) {
        final String result = b.getType().name().contains("LAVA") ? "WATER" : "LAVA";
        for(BlockFace f : faces) {
            final Block t = generated.getRelative(f, 1);
            if(t.getType().name().contains(result)) {
                return true;
            }
        }
        return false;
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void structureGrowEvent(StructureGrowEvent event) {
        for(BlockState b : event.getBlocks()) {
            generated.add(b.getLocation());
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void leavesDecayEvent(LeavesDecayEvent event) {
        final Block b = event.getBlock();
        final Location l = b.getLocation();
        final World w = l.getWorld();
        generated.remove(l);
        final List<ItemStack> drops = new ArrayList<>();
        for(ItemStack d : b.getDrops()) {
            item = d.clone();
            itemMeta = d.getItemMeta(); lore.clear();
            lore.addAll(cosmeticFormat);
            itemMeta.setLore(lore); lore.clear();
            item.setItemMeta(itemMeta);
            drops.add(item);
        }
        event.setCancelled(true);
        w.getBlockAt(l).setType(Material.AIR);
        for(ItemStack i : drops) {
            w.dropItemNaturally(l, i);
        }
    }
    @EventHandler
    private void entityDeathEvent(EntityDeathEvent event) {
        final LivingEntity e = event.getEntity();
        if(!(e instanceof Player)) {
            lore.clear(); lore.addAll(cosmeticFormat);
            for(ItemStack is : event.getDrops()) {
                itemMeta = is.getItemMeta();
                itemMeta.setLore(lore);
                is.setItemMeta(itemMeta);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void playerIslandInteractEvent(IslandInteractEvent event) {
        final PlayerInteractEvent e = event.getEvent();
        final Block b = e.getClickedBlock();
        if(b != null) {
            final Island is = event.getIsland();
            final Player player = event.getPlayer();
            final String a = e.getAction().name();
            final Location bl = b.getLocation();
            if(a.contains("LEFT") && !player.getGameMode().equals(GameMode.CREATIVE)) {
                final ItemStack it = e.getItem();
                final Material type = it != null ? it.getType() : null;
                if(type == null || type.equals(Material.AIR) || it.hasItemMeta() && it.getItemMeta().hasLore() && it.getItemMeta().getLore().containsAll(cosmeticFormat)) {
                    final UUID u = player.getUniqueId();
                    final Island i = Island.players.getOrDefault(u, null), on = Island.valueOf(bl);
                    final RSPlayer pdata = RSPlayer.get(u);
                    if(pdata.instaBreakTutorial) {
                        sendStringListMessage(player, config.getStringList("messages.insta break tutorial"), null);
                        pdata.instaBreakTutorial = false;
                    }
                    if(on != null && on.equals(i) && pdata.getToggles().get(ToggleType.INSTANT_BLOCK_BREAK) && !generated.contains(bl)) {
                        breakBlock(player, pdata, b);
                    }
                }
            }
        }
    }
}