package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.addon.ResourceNode;
import me.randomhashtags.randomsky.addon.active.ActiveResourceNode;
import me.randomhashtags.randomsky.addon.file.FileResourceNode;
import me.randomhashtags.randomsky.addon.island.Island;
import me.randomhashtags.randomsky.addon.island.IslandLevel;
import me.randomhashtags.randomsky.event.island.IslandBreakBlockEvent;
import me.randomhashtags.randomsky.event.island.IslandPlaceBlockEvent;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;

import static java.io.File.separator;

public class ResourceNodes extends RSFeature {
    private static ResourceNodes instance;
    public static ResourceNodes getResourceNodes() {
        if(instance == null) instance = new ResourceNodes();
        return instance;
    }

    public YamlConfiguration config;

    public void load() {
        final long started = System.currentTimeMillis();
        final String folder = DATA_FOLDER + separator + "resources" + separator + "nodes";
        save(folder, "_settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(folder, "_settings.yml"));

        for(File f : new File(folder).listFiles()) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                new FileResourceNode(f);
            }
        }

        sendConsoleMessage("&6[RandomSky] &aLoaded " + RSStorage.getAll(Feature.RESOURCE_NODE).size() + " Resource Nodes &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        RSStorage.unregisterAll(Feature.RESOURCE_NODE);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void islandBlockPlaceEvent(IslandPlaceBlockEvent event) {
        final Island is = event.getIsland();
        final Player player = event.getPlayer();
        final ItemStack i = event.getItem();
        final ResourceNode n = ResourceNode.valueOf(i);
        if(n != null) {
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{IS_CREATOR}", Bukkit.getOfflinePlayer(is.getCreator()).getName());

            final IslandLevel requiredLevel = n.getRequiredIslandLevel();
            final int requiredLvl = requiredLevel != null ? n.getRequiredIslandLevel().getLevel() : -1;
            final String nodeName = n.getNodeName();
            if(is.getAllowedNodes().contains(n) || requiredLevel == null || is.getIslandLevel().getLevel() >= requiredLvl) {
                new ActiveResourceNode(n, event.getEvent().getBlockPlaced().getLocation());
                replacements.put("{TYPE}", nodeName);
                sendStringListMessage(player, getStringList(config, "messages.placed"), replacements);
                for(Player p : is.getOnlineMembers()) {
                    if(p != player) {
                        sendStringListMessage(p, getStringList(config, "messages.placed notify"), replacements);
                    }
                }
            } else {
                event.setCancelled(true);
                replacements.put("{TYPE}", nodeName);
                replacements.put("{REQUIRED_LEVEL}", Integer.toString(requiredLvl));
                sendStringListMessage(player, getStringList(config, "messages.level too low to place node"), replacements);
            }
            player.updateInventory();
        }
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void islandBreakBlockEvent(IslandBreakBlockEvent event) {
        final Island is = event.getIsland();
        final BlockBreakEvent e = event.getEvent();
        final Location l = e.getBlock().getLocation();
        final ActiveResourceNode a = is.valueof(l);
        if(a != null) {
            final Player player = event.getPlayer();
            final HarvestResourceNodeEvent r = new HarvestResourceNodeEvent(player, is, a);
            PLUGIN_MANAGER.callEvent(r);

            final ResourceNode type = a.getType();
            final long cooldown = a.getCooldownExpiration(), time = System.currentTimeMillis();
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{TYPE}", type.getNodeName());
            replacements.put("{TIME}", getRemainingTime(cooldown-time));
            event.setCancelled(true);
            dmgDurability(player.getItemInHand());
            if(player.isSneaking()) {
                a.delete();
                l.getWorld().dropItemNaturally(l, type.getItem());
                sendStringListMessage(player, getStringList(config, "messages.destroyed"), replacements);
            } else if(time >= cooldown) {
                a.harvest(player);
            } else {
                sendStringListMessage(player, getStringList(config, "messages.respawn"), replacements);
                sendStringListMessage(player, getStringList(config, "messages.pickup"), replacements);
            }
        }
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void playerIslandInteractEvent(IslandInteractEvent event) {
        final PlayerInteractEvent e = event.getEvent();
        final Block b = e.getClickedBlock();
        if(b != null) {
            final Island is = event.getIsland();
            final Player player = event.getPlayer();
            final String a = e.getAction().name();
            final Location bl = b.getLocation();
            final ActiveResourceNode n = is.valueof(bl);
            if(a.contains("RIGHT") && n != null) {
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{TYPE}", n.getType().getNodeName());
                final long time = System.currentTimeMillis(), cooldown = n.getCooldownExpiration();
                if(time >= cooldown) {
                    sendStringListMessage(player, getStringList(config, "messages.ready to be harvested"), replacements);
                } else {
                    final String t = getRemainingTime(cooldown-time);
                    replacements.put("{TIME}", t.equals("") ? "0" : t);
                    sendStringListMessage(player, getStringList(config, "messages.respawn"), replacements);
                }
            }
        }
    }
}
