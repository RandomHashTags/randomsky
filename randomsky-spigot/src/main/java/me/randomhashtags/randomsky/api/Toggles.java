package me.randomhashtags.randomsky.api;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.universal.UInventory;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.io.File;

public class Toggles extends RSFeature implements CommandExecutor {
    private static Toggles instance;
    public static Toggles getToggles() {
        if(instance == null) instance = new Toggles();
        return instance;
    }

    public YamlConfiguration config;
    private UInventory inventory;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        viewToggles((Player) sender);
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "toggles.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "toggles.yml"));

        inventory = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));

        sendConsoleMessage("&6[RandomSky] &aLoaded Toggles &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }

    public void viewToggles(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.toggles", true)) {
            player.closeInventory();
            final int size = inventory.getSize();
            player.openInventory(Bukkit.createInventory(player, size, inventory.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(inventory.getInventory().getContents());
            for(int i = 0; i < size; i++) {
            }
            player.updateInventory();
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        if(event.getView().getTitle().equals(inventory.getTitle())) {
            final Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            player.updateInventory();
        }
    }
}
