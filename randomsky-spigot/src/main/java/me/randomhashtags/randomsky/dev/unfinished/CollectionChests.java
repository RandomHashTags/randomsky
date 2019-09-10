package me.randomhashtags.randomsky.dev.unfinished;

import me.randomhashtags.randomsky.util.RSFeature;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class CollectionChests extends RSFeature {
    private static CollectionChests instance;
    public static CollectionChests getCollectionChests() {
        if(instance == null) instance = new CollectionChests();
        return instance;
    }

    public YamlConfiguration config;
    public ItemStack collectionchest;

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "collection chests.yml");
        config = YamlConfiguration.loadConfiguration(new File(rsd, "collection chests.yml"));

        collectionchest = d(config, "settings");
        sendConsoleMessage("&6[RandomSky] &aLoaded Collection Chests &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void blockPlaceEvent(BlockPlaceEvent event) {
        final ItemStack i = event.getItemInHand();
        if(i.isSimilar(collectionchest)) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
    }
}
