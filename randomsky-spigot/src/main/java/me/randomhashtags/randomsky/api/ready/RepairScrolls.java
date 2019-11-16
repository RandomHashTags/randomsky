package me.randomhashtags.randomsky.api.ready;

import me.randomhashtags.randomsky.addon.RepairScroll;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.addon.file.FileRepairScroll;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSStorage;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

import static java.io.File.separator;

public class RepairScrolls extends RSFeature implements Listener {
    private static RepairScrolls instance;
    public static RepairScrolls getRepairScrolls() {
        if(instance == null) instance = new RepairScrolls();
        return instance;
    }

    public YamlConfiguration config;

    public void load() {
        final long started = System.currentTimeMillis();
        save(dataFolder + separator + "repair scrolls", "_settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(dataFolder + separator + "repair scrolls", "_settings.yml"));
        final File folder = new File(dataFolder + separator + "repair scrolls");
        if(folder.exists()) {
            for(File f : folder.listFiles()) {
                new FileRepairScroll(f);
            }
        }
        sendConsoleMessage("&6[RandomSky] &aLoaded " + RSStorage.getAll(Feature.REPAIR_SCROLL).size() + " Repair Scrolls &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        RSStorage.unregisterAll(Feature.REPAIR_SCROLL);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();
        if(current != null && !current.getType().equals(Material.AIR) && cursor != null && !cursor.getType().equals(Material.AIR)) {
            final RepairScroll r = valueOf(cursor);
            final Player player = (Player) event.getWhoClicked();
            if(r != null) {
                event.setCancelled(true);
                if(r.canBeApplied(current)) {
                    final double percent = r.getPercent(cursor)/100.00;
                    final short d = current.getDurability(), max = current.getType().getMaxDurability(), repaired = (short) (max*percent), n = (short) (d-repaired < 0 ? 0 : d-repaired);
                    current.setDurability(n);
                    final int amount = cursor.getAmount();
                    if(amount == 1) cursor = new ItemStack(Material.AIR);
                    else cursor.setAmount(amount-1);
                    event.setCursor(cursor);
                    event.setCurrentItem(current);
                } else {
                    sendStringListMessage(player, config.getStringList("messages.cannot repair item with scroll"), null);
                }
                player.updateInventory();
            }
        }
    }
    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final RepairScroll scroll = valueOf(event.getItem());
        if(scroll != null && event.getAction().name().contains("RIGHT")) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
    }

    public RepairScroll valueOf(ItemStack is) {
        if(repairscrolls != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final List<String> l = is.getItemMeta().getLore();
            for(RepairScroll scroll : repairscrolls.values()) {
                if(is.isSimilar(scroll.getItem(getRemainingInt(l.get(scroll.getPercentSlot()))))) {
                    return scroll;
                }
            }
        }
        return null;
    }
}
