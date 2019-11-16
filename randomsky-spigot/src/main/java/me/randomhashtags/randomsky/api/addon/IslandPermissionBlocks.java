package me.randomhashtags.randomsky.api.addon;

import me.randomhashtags.randomsky.addon.PermissionBlock;
import me.randomhashtags.randomsky.addon.active.ActivePermissionBlock;
import me.randomhashtags.randomsky.addon.island.Island;
import me.randomhashtags.randomsky.api.IslandAddon;
import me.randomhashtags.randomsky.util.RSPlayer;
import me.randomhashtags.randomsky.util.universal.UInventory;
import me.randomhashtags.randomsky.util.universal.UMaterial;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class IslandPermissionBlocks extends IslandAddon {
    private static IslandPermissionBlocks instance;
    public static IslandPermissionBlocks getIslandPermissionBlocks() {
        if(instance == null) instance = new IslandPermissionBlocks();
        return instance;
    }

    public YamlConfiguration config;

    private UInventory gui;
    private ItemStack enabled, disabled, background, regionInfo, regionMembers;
    private String A, D;
    private List<String> addedLore;

    private HashMap<Integer, String> settings, settingsName, editations;
    private HashMap<Integer, List<String>> settingsLore;
    private HashMap<Player, ActivePermissionBlock> editing;

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "island permission blocks.yml");
        config = YamlConfiguration.loadConfiguration(new File(dataFolder, "island permission blocks.yml"));

        A = colorize(config.getString("permission blocks.gui.lores.allowed"));
        D = colorize(config.getString("permission blocks.gui.lores.denied"));
        addedLore = colorizeListString(config.getStringList("permission blocks.gui.lores.added lore"));
        regionMembers = d(config, "permission blocks.gui.region members");
        regionInfo = d(config, "permission blocks.gui.region info");

        settings = new HashMap<>();
        settingsName = new HashMap<>();
        settingsLore = new HashMap<>();
        editations = new HashMap<>();
        editing = new HashMap<>();

        int loaded = 0;
        final int pbsize = config.getInt("permission blocks.gui.size");
        gui = new UInventory(null, pbsize, colorize(config.getString("permission blocks.gui.title")));
        final Inventory gi = gui.getInventory();
        final List<String> prelore = config.getStringList("permission blocks.pre lore");
        for(String s : config.getConfigurationSection("permission blocks").getKeys(false)) {
            if(!s.equals("pre lore") && !s.equals("gui")) {
                final String p = "permission blocks." + s;
                final int radius = config.getInt(p + ".radius");
                item = d(config, p);
                itemMeta = item.getItemMeta(); lore.clear();
                for(String l : prelore) lore.add(colorize(l.replace("{RADIUS}", Integer.toString(radius))));
                if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
                new PermissionBlock(s, item, radius);
                loaded++;
            }
        }

        background = d(config, "permission blocks.gui.background");
        enabled = d(config, "permission blocks.gui.settings.enabled");
        disabled = d(config, "permission blocks.gui.settings.disabled");
        for(int i = 1; i <= 3; i++) {
            final boolean o = i == 1, t = i == 2;
            final String s = o ? "region members" : t ? "region info" : "add member";
            final int slot = config.getInt("permission blocks.gui." + s + ".slot");
            gi.setItem(slot, o ? regionMembers : t ? regionInfo : d(config, "permission blocks.gui." + s));
            editations.put(slot, s);
        }
        for(String s : config.getConfigurationSection("permission blocks.gui.settings").getKeys(false)) {
            if(!s.equals("enabled") && !s.equals("disabled")) {
                final String p = "permission blocks.gui.settings." + s + ".", n = config.getString(p + "name");
                final List<String> l = colorizeListString(config.getStringList(p + "lore"));
                final int slot = config.getInt(p + "slot");
                settings.put(slot, s);
                settingsName.put(slot, n != null ? colorize(n) : null);
                settingsLore.put(slot, l);

                item = disabled.clone();
                gi.setItem(slot, item);
            }
        }
        for(int i = 0; i < pbsize; i++) {
            if(gi.getItem(i) == null) {
                gi.setItem(i, background);
            }
        }
        sendConsoleMessage("&6[RandomSky] &aLoaded " + loaded + " Islands Permission Blocks &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }

    public void viewPermissionBlock(Player player, ActivePermissionBlock block) {
        if(hasPermission(player, "RandomSky.island.view.permissionblock", true)) {
            final int size = gui.getSize();
            player.openInventory(Bukkit.createInventory(player, size, gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());

            for(int i = 0; i < size; i++) {
                if(settings.containsKey(i)) {
                    final boolean allowed = isAllowed(block, settings.get(i));
                    item = (allowed ? enabled : disabled).clone(); itemMeta = item.getItemMeta(); lore.clear();
                    itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{NAME}", settingsName.get(i)));
                    lore.addAll(settingsLore.get(i));
                    lore.add(allowed ? A : D);
                    lore.addAll(addedLore);
                    itemMeta.setLore(lore); lore.clear();
                    item.setItemMeta(itemMeta);
                    top.setItem(i, item);
                }
            }
            updateRegionInfo(player, top, block);
            editing.put(player, block);
        }
    }
    private void updateSetting(Player player, Inventory top, int slot, ActivePermissionBlock block) {
        if(settings.containsKey(slot)) {
            final boolean allowed = isAllowed(block, settings.get(slot));
            item = (allowed ? enabled : disabled).clone(); itemMeta = item.getItemMeta(); lore.clear();
            itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{NAME}", settingsName.get(slot)));
            lore.addAll(settingsLore.get(slot));
            lore.add(allowed ? A : D);
            lore.addAll(addedLore);
            itemMeta.setLore(lore); lore.clear();
            item.setItemMeta(itemMeta);
            top.setItem(slot, item);
            updateRegionInfo(player, top, block);
        }
    }
    private void updateRegionInfo(Player player, Inventory top, ActivePermissionBlock block) {
        final String r = Integer.toString(block.getType().getRadius()), A = colorize(config.getString("permission blocks.gui.lores.allowed")), D = colorize(config.getString("permission blocks.gui.lores.denied"));
        final HashMap<String, String> e = new HashMap<>();
        for(int i : settings.keySet()) {
            final String s = settings.get(i);
            e.put("{" + s.toUpperCase().replace(" ", "_") + "}", isAllowed(block, s) ? A : D);
        }

        for(int i : editations.keySet()) {
            final String s = editations.get(i);
            if(s.equals("region info")) {
                item = regionInfo.clone(); itemMeta = item.getItemMeta(); lore.clear();
                if(itemMeta.hasLore()) {
                    for(String p : itemMeta.getLore()) {
                        for(String o : e.keySet()) p = p.replace(o, e.get(o));
                        lore.add(p.replace("{RADIUS}", r));
                    }
                }
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
                top.setItem(i, item);
                updateRegionMembers(player, top, block);
                return;
            }
        }
    }
    private void updateRegionMembers(Player player, Inventory top, ActivePermissionBlock block) {
        final List<String> r = colorizeListString(config.getStringList("permission blocks.gui.lores." + (block.publicRegion ? "public region" : block.members.isEmpty() ? "no members" : "members")));
        final List<String> m = new ArrayList<>();
        for(UUID u : block.members) m.add(Bukkit.getOfflinePlayer(u).getName());

        for(int i : editations.keySet()) {
            final String s = editations.get(i);
            if(s.equals("region members")) {
                item = regionMembers.clone(); itemMeta = item.getItemMeta(); lore.clear();
                if(itemMeta.hasLore()) {
                    for(String w : itemMeta.getLore()) {
                        if(w.equals("{MEMBERS}")) {
                            for(String p : r) {
                                if(p.contains("{MEMBERS}")) {
                                    for(String q : m) {
                                        lore.add(p.replace("{MEMBERS}", q));
                                    }
                                } else {
                                    lore.add(p);
                                }
                            }
                        } else {
                            lore.add(w);
                        }
                    }
                }
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
                top.setItem(i, item);
                player.updateInventory();
                return;
            }
        }
    }
    private boolean isAllowed(ActivePermissionBlock block, String setting) {
        if(setting.equals("interact")) return block.interact;
        else if(setting.equals("interact with doors")) return block.interactWithDoors;
        else if(setting.equals("interact with levers")) return block.interactWithLevers;
        else if(setting.equals("interact with pressure plates")) return block.interactWithPressurePlates;
        else if(setting.equals("interact with hoppers")) return block.interactWithHoppers;
        else if(setting.equals("interact with chests")) return block.interactWithChests;
        else if(setting.equals("interact with entities")) return block.interactWithEntities;
        else if(setting.equals("damage entities")) return block.damageEntities;
        else if(setting.equals("pickup exp")) return block.pickupExp;
        else if(setting.equals("pickup items")) return block.pickupItems;
        else if(setting.equals("drop items")) return block.dropItems;
        else if(setting.equals("placed blocks")) return block.placeBlocks;
        else if(setting.equals("break blocks")) return block.breakBlocks;
        else if(setting.equals("harvest resource nodes")) return block.harvestResourceNodes;
        else if(setting.equals("public region")) return block.publicRegion;
        else if(setting.equals("safe pvp")) return block.safePVP;
        else return false;
    }
    private void toggleSetting(Player player, ActivePermissionBlock block, int slot) {
        if(settings.containsKey(slot)) {
            final String setting = settings.get(slot);
            if(setting.equals("interact")) block.interact = !block.interact;
            else if(setting.equals("interact with doors")) block.interactWithDoors = !block.interactWithDoors;
            else if(setting.equals("interact with levers")) block.interactWithLevers = !block.interactWithLevers;
            else if(setting.equals("interact with pressure plates")) block.interactWithPressurePlates = !block.interactWithPressurePlates;
            else if(setting.equals("interact with hoppers")) block.interactWithHoppers = !block.interactWithHoppers;
            else if(setting.equals("interact with chests")) block.interactWithChests = !block.interactWithChests;
            else if(setting.equals("interact with entities")) block.interactWithEntities = !block.interactWithEntities;
            else if(setting.equals("damage entities")) block.damageEntities = !block.damageEntities;
            else if(setting.equals("pickup exp")) block.pickupExp = !block.pickupExp;
            else if(setting.equals("pickup items")) block.pickupItems = !block.pickupItems;
            else if(setting.equals("drop items")) block.dropItems = !block.dropItems;
            else if(setting.equals("placed blocks")) block.placeBlocks = !block.placeBlocks;
            else if(setting.equals("break blocks")) block.breakBlocks = !block.breakBlocks;
            else if(setting.equals("harvest resource nodes")) block.harvestResourceNodes = !block.harvestResourceNodes;
            else if(setting.equals("public region")) block.publicRegion = !block.publicRegion;
            else if(setting.equals("safe pvp")) block.safePVP = !block.safePVP;
            else return;
            updateSetting(player, player.getOpenInventory().getTopInventory(), slot, block);
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        if(top.getHolder() == player && event.getView().getTitle().equals(gui.getTitle())) {
            event.setCancelled(true);
            player.updateInventory();

            final boolean editing = this.editing.containsKey(player);
            final int r = event.getRawSlot();
            final ItemStack c = event.getCurrentItem();
            if(r < 0 || r >= top.getSize() || c == null || c.getType().equals(Material.AIR) || !editing) return;
            final ActivePermissionBlock a = this.editing.get(player);
            if(settings.containsKey(r)) {
                toggleSetting(player, a, r);
                final boolean n = isAllowed(a, settings.get(r));
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{SETTING}", ChatColor.stripColor(c.getItemMeta().getDisplayName()));
                sendStringListMessage(player, config.getStringList("messages.setting " + (n ? "enable" : "disable")), replacements);
            }
            player.updateInventory();
        }
    }

    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        editing.remove(player);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void playerIslandBreakBlockEvent(PlayerIslandBreakBlockEvent event) {
        final Player player = event.player;
        final UUID uuid = player.getUniqueId();
        final Island is = event.island;
        final BlockBreakEvent e = event.breakEvent;
        final Block b = e.getBlock();
        final Location l = b.getLocation();
        final World w = l.getWorld();
        final ActivePermissionBlock a = is.valueOF(l);
        final List<ActivePermissionBlock> nearby = is.getNearbyPermissionBlocks(l);
        if(!is.members.containsKey(uuid)) {
            if(!nearby.isEmpty()) {
                final String mat = UMaterial.getItem(b).name();
                final boolean isDoor = mat.contains("DOOR"), isLever = mat.equals("LEVER"), isHopper = mat.equals("HOPPER"), isChest = mat.equals("CHEST") || mat.equals("TRAPPED_CHEST") || mat.equals("ENDER_CHEST");
                for(ActivePermissionBlock p : nearby) {
                    if(p.publicRegion || p.members.contains(uuid)) {
                        if(isDoor && p.interactWithDoors
                                || isLever && p.interactWithLevers
                                || isHopper && p.interactWithHoppers
                                || isChest && p.interactWithChests
                                || !isDoor && !isLever && !isHopper && !isChest && p.interact) {
                            return;
                        } else {
                            event.setCancelled(true);
                            sendStringListMessage(player, config.getStringList("messages.no permission to break blocks in protected region"), null);
                        }
                        return;
                    }
                }
            } else {
                event.setCancelled(true);
                sendStringListMessage(player, config.getStringList("messages.no permission to break blocks in protected region"), null);
            }
        } else if(a != null) {
            final PermissionBlock type = a.getType();
            event.setCancelled(true);
            dmgDurability(player.getItemInHand());
            is.permissionBlocks.remove(a);
            a.delete();
            w.dropItemNaturally(l.clone().add(0.5, 0.5, 0.5), type.item());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void playerIslandInteractEvent(PlayerIslandInteractEvent event) {
        final PlayerInteractEvent e = event.interactEvent;
        final Block b = e.getClickedBlock();
        if(!e.isCancelled() && b != null) {
            final ItemStack it = e.getItem();
            if(it == null || it.getType().equals(Material.AIR) || mining.isEnabled && it.hasItemMeta() && it.getItemMeta().hasLore() && it.getItemMeta().getLore().contains(cosmeticFormat)) {
                final String a = e.getAction().name();
                final Location bl = b.getLocation();
                final Player player = event.player;
                final UUID u = player.getUniqueId();
                final Island is = event.island;
                final HashMap<UUID, IslandRole> members = is.members;
                final ActivePermissionBlock pb = is.valueOF(bl);
                final List<ActivePermissionBlock> nearby = is.getNearbyPermissionBlocks(bl);
                final String mat = UMaterial.getItem(b).name();
                final boolean isMember = members.containsKey(u), isDoor = mat.contains("DOOR"), isLever = mat.equals("LEVER"), isHopper = mat.equals("HOPPER"), isChest = mat.equals("CHEST") || mat.equals("TRAPPED_CHEST") || mat.equals("ENDER_CHEST");
                if(a.contains("RIGHT")) {
                    if(!isMember) {
                        if(!nearby.isEmpty()) {
                            for(ActivePermissionBlock p : nearby) {
                                if(p.publicRegion || p.members.contains(u)) {
                                    if(isDoor && p.interactWithDoors
                                            || isLever && p.interactWithLevers
                                            || isHopper && p.interactWithHoppers
                                            || isChest && p.interactWithChests
                                            || !isDoor && !isLever && !isHopper && !isChest && p.interact) {
                                        return;
                                    } else {
                                        event.setCancelled(true);
                                        sendStringListMessage(player, config.getStringList("messages.no permission to interact in protected region"), null);
                                    }
                                    return;
                                }
                            }
                        } else {
                            event.setCancelled(true);
                            sendStringListMessage(player, config.getStringList("messages.no permission to interact in protected region"), null);
                        }
                    } else if(pb != null) {
                        viewPermissionBlock(player, pb);
                    } else {

                    }
                } else if(a.contains("LEFT")) {
                    if(pb != null) {
                        event.setCancelled(true);
                        if(!isMember) {
                            sendStringListMessage(player, config.getStringList("messages.no permission to interact in protected region"), null);
                        } else {
                            final World w = bl.getWorld();
                            final PermissionBlock type = pb.getType();
                            is.permissionBlocks.remove(pb);
                            pb.delete();
                            w.dropItemNaturally(bl.clone().add(0.5, 0.5, 0.5), type.item());
                            spawnParticle(RSPlayer.get(u), w, bl, type.item());
                        }
                    } else if(!isMember) {
                        if(!nearby.isEmpty()) {
                            for(ActivePermissionBlock p : nearby) {
                                if(p.publicRegion || p.members.contains(u)) {
                                    final boolean i = p.interact;
                                    if(i && p.breakBlocks) {
                                        return;
                                    } else {
                                        event.setCancelled(true);
                                        sendStringListMessage(player, config.getStringList("messages.no permission to " + (!i ? "interact" : "break blocks") + " in protected region"), null);
                                    }
                                    return;
                                }
                            }
                        } else {
                            event.setCancelled(true);
                            sendStringListMessage(player, config.getStringList("messages.no permission to interact in protected region"), null);
                        }
                    }
                }
            }
        }
    }
}
