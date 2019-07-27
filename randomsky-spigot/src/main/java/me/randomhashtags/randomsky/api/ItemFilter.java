package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.addons.FilterCategory;
import me.randomhashtags.randomsky.addons.usingfile.FileFilterCategory;
import me.randomhashtags.randomsky.utils.RSFeature;
import me.randomhashtags.randomsky.utils.RSPlayer;
import me.randomhashtags.randomsky.utils.universal.UInventory;
import me.randomhashtags.randomsky.utils.universal.UMaterial;
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

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class ItemFilter extends RSFeature implements CommandExecutor {
    private static ItemFilter instance;
    public static ItemFilter getItemFilter() {
        if(instance == null) instance = new ItemFilter();
        return instance;
    }

    public YamlConfiguration config;
    private UInventory gui;
    private String enablePrefix, disabledPrefix;
    private List<String> enable, disable, addedLore;
    private HashMap<Integer, FilterCategory> categorySlots;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) return true;
        final Player player = (Player) sender;
        final int l = args.length;
        if(l == 0) {
            viewHelp(player);
        } else {
            final String a = args[0];
            if(a.equals("toggle")) {
                toggleFilter(player);
            } else if(a.equals("edit")) {
                viewCategories(player);
            }
        }
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "item filter.yml");
        config = YamlConfiguration.loadConfiguration(new File(randomsky.getDataFolder(), "item filter.yml"));

        categorySlots = new HashMap<>();

        addedLore = colorizeListString(config.getStringList("settings.categories added lore"));
        enablePrefix = ChatColor.translateAlternateColorCodes('&', config.getString("settings.enabled prefix"));
        enable = colorizeListString(config.getStringList("settings.enabled lore"));
        disabledPrefix = ChatColor.translateAlternateColorCodes('&', config.getString("settings.disabled prefix"));
        disable = colorizeListString(config.getStringList("settings.disabled lore"));

        gui = new UInventory(null, config.getInt("categories.size"), ChatColor.translateAlternateColorCodes('&', config.getString("categories.title")));
        final Inventory gi = gui.getInventory();
        final String folder = rpd + separator + "filter categories";
        for(String s : config.getConfigurationSection("categories").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size")) {
                final String p = "categories." + s + ".", opens = config.getString(p + "opens");
                final int slot = config.getInt(p + "slot");
                item = d(config, "categories." + s); itemMeta = item.getItemMeta();
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
        config = null;
        gui = null;
        enablePrefix = null;
        disabledPrefix = null;
        enable = null;
        disable = null;
        addedLore = null;
        categorySlots = null;
        instance = null;
    }

    public void viewHelp(Player player) {
        if(hasPermission(player, "RandomSky.filter.help", true)) {
            sendStringListMessage(player, config.getStringList("messages.help"), null);
        }
    }


    public void viewCategories(Player player) {
        if(hasPermission(player, "RandomSky.filter.view", true)) {
            player.closeInventory();
            player.openInventory(Bukkit.createInventory(player, gui.getSize(), gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());
            player.updateInventory();
        }
    }
    private ItemStack getStatus(List<UMaterial> filtered, ItemStack is) {
        itemMeta = is.getItemMeta(); lore.clear();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
        final UMaterial u = UMaterial.match(is);
        final boolean isFiltered = filtered.contains(u);
        itemMeta.setDisplayName((isFiltered ? enablePrefix : disabledPrefix) + ChatColor.stripColor(itemMeta.getDisplayName()));
        itemMeta.setLore(isFiltered ? enable : disable);
        is.setItemMeta(itemMeta);
        if(isFiltered) is.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        return is;
    }
    public void toggleFilter(Player player) {
        if(hasPermission(player, "RandomSky.filter.toggle", true)) {
            final RSPlayer pdata = RSPlayer.get(player.getUniqueId());
            pdata.filter = !pdata.filter;
            sendStringListMessage(player, config.getStringList("messages." + (pdata.filter ? "en" : "dis") + "able"), null);
        }
    }
    public void viewCategory(Player player, FilterCategory category) {
        if(category != null && player != null && hasPermission(player, "RandomSky.filter.view." + category.getIdentifier(), true)) {
            player.closeInventory();
            final List<UMaterial> filtered = RSPlayer.get(player.getUniqueId()).getFilteredItems();
            final UInventory target = category.getInventory();
            final int size = target.getSize();
            player.openInventory(Bukkit.createInventory(player, size, target.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(target.getInventory().getContents());
            for(int i = 0; i < size; i++) {
                if(top.getItem(i) != null) {
                    top.setItem(i, getStatus(filtered, top.getItem(i).clone()));
                }
            }
            player.updateInventory();
        }
    }

    private FilterCategory valueOf(String title) {
        if(title != null && filtercategories != null) {
            for(FilterCategory c : filtercategories.values()) {
                if(c.getTitle().equals(title)) {
                    return c;
                }
            }
        }
        return null;
    }


    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent event) {
        if(!event.isCancelled()) {
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
                    final List<UMaterial> filtered = RSPlayer.get(player.getUniqueId()).getFilteredItems();
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
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private void playerPickupItemEvent(PlayerPickupItemEvent event) {
        if(!event.isCancelled()) {
            final Player player = event.getPlayer();
            final RSPlayer pdata = RSPlayer.get(player.getUniqueId());
            if(pdata.filter) {
                final ItemStack i = event.getItem().getItemStack();
                final UMaterial u = UMaterial.match(i);
                if(!pdata.getFilteredItems().contains(u)) {
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        final FilterCategory c = valueOf(event.getView().getTitle());
        if(c != null) {
            scheduler.scheduleSyncDelayedTask(randomsky, () -> viewCategories(player), 0);
        }
    }
}
