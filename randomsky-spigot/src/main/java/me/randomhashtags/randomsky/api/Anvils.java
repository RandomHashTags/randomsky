package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.universal.UInventory;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RandomSkyFeature;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public enum Anvils implements RSFeature {
    INSTANCE;

    public YamlConfiguration config;

    private List<String> cannotPutItemIn, nothingToCombine;
    private ItemStack background, confirm, invalidRecipe;
    private UInventory inventory;

    private List<Player> viewing;

    @Override
    public @NotNull RandomSkyFeature get_feature() {
        return RandomSkyFeature.ANVILS;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "anvils.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "anvils.yml"));

        cannotPutItemIn = getStringList(config, "messages.cannot put item in");
        nothingToCombine = getStringList(config, "messages.nothing to combine");

        background = d(config, "items.background");
        confirm = d(config, "items.confirm");
        invalidRecipe = d(config, "items.invalid recipe");

        inventory = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        final Inventory inv = inventory.getInventory();
        final ConfigurationSection gui = config.getConfigurationSection("gui");
        for(String key : gui.getKeys(false)) {
            if(!key.equals("title") && !key.equals("size")) {
                final String i = config.getString("gui." + key + ".item");
                inv.setItem(config.getInt("gui." + key + ".slot"), i != null ? i.equalsIgnoreCase("recipe") ? invalidRecipe : i.equalsIgnoreCase("confirm") ? confirm : d(config, "gui." + key) : null);
            }
        }

        viewing = new ArrayList<>();

        sendConsoleMessage("&6[RandomSky] &aLoaded Anvils &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    @Override
    public void unload() {
        for(Player player : new ArrayList<>(viewing)) {
            player.closeInventory();
        }
    }

    public void view(@NotNull Player player) {
        player.openInventory(Bukkit.createInventory(player, inventory.getSize(), inventory.getTitle()));
        player.getOpenInventory().getTopInventory().setContents(inventory.getInventory().getContents());
        player.updateInventory();
        viewing.add(player);
    }

    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        viewing.remove(player);
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        if(viewing.contains(player)) {
            event.setCancelled(true);
            player.updateInventory();
        }
    }
}
