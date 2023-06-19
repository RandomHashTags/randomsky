package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.addon.ColorCrystal;
import me.randomhashtags.randomsky.util.*;
import me.randomhashtags.randomsky.universal.UInventory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public enum ColorCrystals implements RSFeature {
    INSTANCE;

    public YamlConfiguration config;
    private UInventory inventory;
    private ItemStack background, locked;
    private List<String> dontHaveUnlockedMsg;

    @Override
    public @NotNull RandomSkyFeature get_feature() {
        return RandomSkyFeature.COLOR_CRYSTALS;
    }

    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "color crystals.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "color crystals.yml"));
        sendConsoleMessage("&6[RandomSky] &aLoaded " + RSStorage.getAll(Feature.COLOR_CRYSTAL).size() + " Color Crystals &e(took " + (System.currentTimeMillis()-started) + "ms)");

        dontHaveUnlockedMsg = getStringList(config, "messages.dont have unlocked");

        inventory = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        background = d(config, "gui.background");
        locked = d(config, "gui.locked");
    }
    @Override
    public void unload() {
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void playerChatEvent(AsyncPlayerChatEvent event) {
        final FileRSPlayer pdata = FileRSPlayer.get(event.getPlayer().getUniqueId());
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
