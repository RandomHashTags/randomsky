package me.randomhashtags.randomsky.utils.addons;

import me.randomhashtags.randomsky.addons.FilterCategory;
import me.randomhashtags.randomsky.utils.RSAddon;
import me.randomhashtags.randomsky.utils.universal.UInventory;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;

import java.io.File;

public class FileFilterCategory extends RSAddon implements FilterCategory {
    private UInventory gui;
    public FileFilterCategory(File f) {
        load(f);
        addFilterCategory(this);
    }
    public String getIdentifier() { return getYamlName(); }

    public String getTitle() { return ChatColor.translateAlternateColorCodes('&', yml.getString("title")); }
    public UInventory getInventory() {
        if(gui == null) {
            gui = new UInventory(null, yml.getInt("size"), getTitle());
            final Inventory i = gui.getInventory();
            for(String s : yml.getConfigurationSection("gui").getKeys(false)) {
                i.setItem(yml.getInt("gui." + s + ".slot"), api.d(yml, "gui." + s));
            }
        }
        return gui;
    }
}