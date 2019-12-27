package me.randomhashtags.randomsky.addon.file;

import me.randomhashtags.randomsky.addon.FilterCategory;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSAddon;
import me.randomhashtags.randomsky.util.RSStorage;
import me.randomhashtags.randomsky.universal.UInventory;
import org.bukkit.inventory.Inventory;

import java.io.File;

public class FileFilterCategory extends RSAddon implements FilterCategory {
    private UInventory gui;
    public FileFilterCategory(File f) {
        load(f);
        RSStorage.register(Feature.FILTER_CATEGORY, this);
    }
    public String getIdentifier() { return getYamlName(); }

    public String getTitle() { return colorize(yml.getString("title")); }
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
