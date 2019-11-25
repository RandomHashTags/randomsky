package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.addon.ColorCrystal;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSPlayer;
import me.randomhashtags.randomsky.util.RSStorage;
import me.randomhashtags.randomsky.util.universal.UInventory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class ColorCrystals extends RSFeature {
    private static ColorCrystals instance;
    public static ColorCrystals getColorCrystals() {
        if(instance == null) instance = new ColorCrystals();
        return instance;
    }

    public YamlConfiguration config;
    private UInventory inventory;
    private ItemStack background, locked;
    private List<String> dontHaveUnlockedMsg;

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "color crystals.yml");
        config = YamlConfiguration.loadConfiguration(new File(dataFolder, "color crystals.yml"));
        sendConsoleMessage("&6[RandomSky] &aLoaded " + RSStorage.getAll(Feature.COLOR_CRYSTAL).size() + " Color Crystals &e(took " + (System.currentTimeMillis()-started) + "ms)");

        dontHaveUnlockedMsg = colorizeListString(config.getStringList("messages.dont have unlocked"));

        inventory = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        background = d(config, "gui.background");
        locked = d(config, "gui.locked");
    }
    public void unload() {
        RSStorage.unregisterAll(Feature.COLOR_CRYSTAL);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void playerChatEvent(AsyncPlayerChatEvent event) {
        final RSPlayer pdata = RSPlayer.get(event.getPlayer().getUniqueId());
        final ColorCrystal active = pdata.getActiveColorCrystal();
        if(active != null) {
            event.setMessage(active.getEffect() + event.getMessage());
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        if(event.getView().getTitle().equals(inventory.getTitle())) {
            final Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            player.updateInventory();
            if(!background.equals(event.getCurrentItem())) {
                sendStringListMessage(player, dontHaveUnlockedMsg, null);
            }
        }
    }
}
