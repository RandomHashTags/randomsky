package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.addon.island.Island;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.FileRSPlayer;
import me.randomhashtags.randomsky.universal.UInventory;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
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
import java.util.HashMap;
import java.util.List;

public enum EnchantmentTables implements RSFeature {
    INSTANCE;

    public YamlConfiguration config;
    private List<String> dontHaveRequiredIslandLevel, help, notEnoughExp;
    private ItemStack background, locked;
    private UInventory inventory;

    private List<Player> viewing;
    private HashMap<Integer, Integer> requiredIslandLevels;

    @Override
    public @NotNull Feature get_feature() {
        return Feature.ENCHANTMENT_TABLES;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "enchantment tables.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "enchantment tables.yml"));

        dontHaveRequiredIslandLevel = getStringList(config, "messages.dont have required island level");
        help = getStringList(config, "messages.help");
        notEnoughExp = getStringList(config, "messages.not enough exp");

        background = d(config, "items.background");
        locked = d(config, "items.locked");

        inventory = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        requiredIslandLevels = new HashMap<>();
        final Inventory inv = inventory.getInventory();
        final ConfigurationSection gui = config.getConfigurationSection("gui");
        for(String key : gui.getKeys(false)) {
            if(!key.equals("title") && !key.equals("size")) {
                final Object isLevel = config.get("gui." + key + ".required is level", null);
                final int slot = config.getInt("gui." + key + ".slot");
                if(isLevel != null) {
                    requiredIslandLevels.put(slot, (int) isLevel);
                }
                inv.setItem(slot, d(config, "gui." + key));
            }
        }

        viewing = new ArrayList<>();

        sendConsoleMessage("&6[RandomSky] &aLoaded Enchantment Tables &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        for(Player player : new ArrayList<>(viewing)) {
            player.closeInventory();
        }
    }

    public void view(@NotNull Player player) {
        player.openInventory(Bukkit.createInventory(player, inventory.getSize(), inventory.getTitle().replace("{XP}", formatInt(getTotalExperience(player)))));
        player.getOpenInventory().getTopInventory().setContents(inventory.getInventory().getContents());
        player.updateInventory();
        viewing.add(player);
    }
    public void viewHelp(@NotNull CommandSender sender) {
        sendStringListMessage(sender, help, null);
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

            final int r = event.getRawSlot();
            final FileRSPlayer pdata = FileRSPlayer.get(player.getUniqueId());
            final Island is = pdata.getIsland();
            if(requiredIslandLevels.containsKey(r)) {
                final HashMap<String, String> replacements = new HashMap<>();
                final int level = is == null ? 0 : is.getIslandLevel().getLevel(), req = requiredIslandLevels.get(r);
                replacements.put("{REQUIRED}", Integer.toString(req));
                replacements.put("{LEVEL}", Integer.toString(level));
                if(level < req) {
                    sendStringListMessage(player, dontHaveRequiredIslandLevel, replacements);
                } else {
                }
            }
        }
    }
}
