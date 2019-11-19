package me.randomhashtags.randomsky.api;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randomsky.addon.ItemRecipe;
import me.randomhashtags.randomsky.addon.file.FileItemRecipe;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSStorage;
import me.randomhashtags.randomsky.util.universal.UInventory;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

import static java.io.File.separator;

public class ItemRecipes extends RSFeature implements CommandExecutor {
    private static ItemRecipes instance;
    public static ItemRecipes getItemRecipes() {
        if(instance == null) instance = new ItemRecipes();
        return instance;
    }

    private UInventory inventory;
    private ItemStack background, locked;
    private List<String> recipesCanBeObtainedMsg;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        viewRecipes((Player) sender);
        return true;
    }

    public YamlConfiguration config;

    public void load() {
        final long started = System.currentTimeMillis();
        final String folder = dataFolder + separator + "item recipes";
        save(folder, "_settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(folder, "_settings.yml"));

        recipesCanBeObtainedMsg = colorizeListString(config.getStringList("messages.recipes can be obtained"));
        inventory = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        background = d(config, "gui.background");
        locked = d(config, "gui.locked");
        final Inventory inv = inventory.getInventory();

        for(File f : new File(folder).listFiles()) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                final ItemRecipe i = new FileItemRecipe(f);
                inv.setItem(i.getSlot(), i.getItem());
            }
        }

        for(int i = 0; i < inv.getSize(); i++) {
            item = inv.getItem(i);
            if(item == null) {
                inv.setItem(i, background);
            } else {
                itemMeta = item.getItemMeta(); lore.clear();
            }
        }

        sendConsoleMessage("&6[RandomSky] &aLoaded " + RSStorage.getAll(Feature.ITEM_RECIPE).size() + " Item Recipes &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        RSStorage.unregisterAll(Feature.ITEM_RECIPE);
    }

    public void viewRecipes(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.itemrecipes", true)) {
            player.closeInventory();
            player.openInventory(Bukkit.createInventory(player, inventory.getSize(), inventory.getTitle()));
            player.getOpenInventory().getTopInventory().setContents(inventory.getInventory().getContents());
            player.updateInventory();
        }
    }

    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        if(event.getView().getTitle().equals(inventory.getTitle())) {
            final Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            player.updateInventory();
            if(!background.equals(event.getCurrentItem())) {
                sendStringListMessage(player, recipesCanBeObtainedMsg, null);
            }
        }
    }
}
