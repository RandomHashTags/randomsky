package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.addon.FilterCategory;
import me.randomhashtags.randomsky.addon.file.FileFilterCategory;
import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.util.*;
import me.randomhashtags.randomsky.universal.UInventory;
import me.randomhashtags.randomsky.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static java.io.File.separator;

public enum ItemFilter implements RSFeature, CommandExecutor {
    INSTANCE;

    public YamlConfiguration config;
    public UInventory gui;
    private String enablePrefix, disabledPrefix;
    private List<String> enable, disable, addedLore;
    private HashMap<Integer, FilterCategory> categorySlots;

    @Override
    public @NotNull RandomSkyFeature get_feature() {
        return RandomSkyFeature.ITEM_FILTER;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) return true;
        final Player player = (Player) sender;
        final int l = args.length;
        if(l == 0) {
            viewHelp(player);
        } else {
            switch (args[0]) {
                case "toggle":
                    toggleFilter(player);
                    break;
                case "edit":
                    viewCategories(player);
                    break;
                default:
                    viewHelp(player);
                    break;
            }
        }
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(DATA_FOLDER + separator + "filter categories", "_settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER + separator + "filter categories", "_settings.yml"));

        categorySlots = new HashMap<>();

        addedLore = getStringList(config, "settings.categories added lore");
        enablePrefix = colorize(config.getString("settings.enabled prefix"));
        enable = getStringList(config, "settings.enabled lore");
        disabledPrefix = colorize(config.getString("settings.disabled prefix"));
        disable = getStringList(config, "settings.disabled lore");

        gui = new UInventory(null, config.getInt("categories.size"), colorize(config.getString("categories.title")));
        final Inventory gi = gui.getInventory();
        final String folder = DATA_FOLDER + separator + "filter categories";
        for(String s : config.getConfigurationSection("categories").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size")) {
                final String p = "categories." + s + ".", opens = config.getString(p + "opens");
                final int slot = config.getInt(p + "slot");
                final ItemStack item = d(config, "categories." + s);
                final ItemMeta itemMeta = item.getItemMeta();
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
                itemMeta.setLore(addedLore);
                item.setItemMeta(itemMeta);
                gi.setItem(slot, item);
                categorySlots.put(slot, new FileFilterCategory(new File(folder, opens + ".yml")));
            }
        }
        sendConsoleMessage("&6[RandomSky] &aLoaded Item Filter &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        RSStorage.unregisterAll(Feature.FILTER_CATEGORY);
    }

    public void viewHelp(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.filter.help", true)) {
            sendStringListMessage(player, getStringList(config, "messages.help"), null);
        }
    }

    public void viewCategories(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.filter.view", true)) {
            player.closeInventory();
            player.openInventory(Bukkit.createInventory(player, gui.getSize(), gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());
            player.updateInventory();
        }
    }
    private ItemStack getStatus(Set<UMaterial> filtered, ItemStack is) {
        final ItemMeta itemMeta = is.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
        final UMaterial u = UMaterial.match(is);
        final boolean isFiltered = filtered.contains(u);
        itemMeta.setDisplayName((isFiltered ? enablePrefix : disabledPrefix) + ChatColor.stripColor(itemMeta.getDisplayName()));
        itemMeta.setLore(isFiltered ? enable : disable);
        is.setItemMeta(itemMeta);
        if(isFiltered) {
            is.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        }
        return is;
    }
    public void toggleFilter(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.filter.toggle", true)) {
            final FileRSPlayer pdata = FileRSPlayer.get(player.getUniqueId());
            final boolean active = pdata.toggleFilter();
            sendStringListMessage(player, getStringList(config, "messages." + (active ? "en" : "dis") + "able"), null);
        }
    }
    public void viewCategory(@NotNull Player player, @NotNull FilterCategory category) {
        if(category != null && player != null && hasPermission(player, "RandomSky.filter.view." + category.getIdentifier(), true)) {
            player.closeInventory();
            final Set<UMaterial> filtered = FileRSPlayer.get(player.getUniqueId()).getFilteredItems();
            final UInventory target = category.getInventory();
            final int size = target.getSize();
            player.openInventory(Bukkit.createInventory(player, size, target.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(target.getInventory().getContents());
            for(int i = 0; i < size; i++) {
                final ItemStack is = top.getItem(i);
                if(is != null) {
                    top.setItem(i, getStatus(filtered, is.clone()));
                }
            }
            player.updateInventory();
        }
    }

    private FilterCategory valueOf(String title) {
        final List<Identifiable> list = RSStorage.getAll(Feature.FILTER_CATEGORY);
        if(title != null && !list.isEmpty()) {
            final List<FilterCategory> categories = (List<FilterCategory>) list;
            for(FilterCategory c : categories) {
                if(c.getTitle().equals(title)) {
                    return c;
                }
            }
        }
        return null;
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final String t = event.getView().getTitle();
        final FilterCategory category = valueOf(event.getView().getTitle());
        if(t.equals(gui.getTitle()) || category != null) {
            final Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            player.updateInventory();
            final ItemStack c = event.getCurrentItem();
            final int r = event.getRawSlot();
            final Inventory top = player.getOpenInventory().getTopInventory();
            if(r < 0 || r >= top.getSize() || c == null || c.getType().equals(Material.AIR)) return;

            if(category != null) {
                final Set<UMaterial> filtered = FileRSPlayer.get(player.getUniqueId()).getFilteredItems();
                final UMaterial target = UMaterial.match(c);
                if(filtered.contains(target)) {
                    filtered.remove(target);
                } else {
                    filtered.add(target);
                }
                top.setItem(r, getStatus(filtered, c));
                player.updateInventory();
            } else if(categorySlots.containsKey(r)) {
                player.closeInventory();
                viewCategory(player, categorySlots.get(r));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void playerPickupItemEvent(PlayerPickupItemEvent event) {
        final Player player = event.getPlayer();
        final FileRSPlayer pdata = FileRSPlayer.get(player.getUniqueId());
        if(pdata.hasActiveFilter()) {
            final ItemStack i = event.getItem().getItemStack();
            final UMaterial u = UMaterial.match(i);
            if(!pdata.getFilteredItems().contains(u)) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        final FilterCategory c = valueOf(event.getView().getTitle());
        if(c != null) {
            SCHEDULER.scheduleSyncDelayedTask(RANDOM_SKY, () -> viewCategories(player), 0);
        }
    }
}
