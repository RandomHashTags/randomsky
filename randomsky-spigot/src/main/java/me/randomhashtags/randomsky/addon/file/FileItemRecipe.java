package me.randomhashtags.randomsky.addon.file;

import me.randomhashtags.randomsky.RandomSkyAPI;
import me.randomhashtags.randomsky.addon.ItemRecipe;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSAddon;
import me.randomhashtags.randomsky.util.RSStorage;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class FileItemRecipe extends RSAddon implements ItemRecipe {
    private int slot = -999;
    private String name;
    private ItemStack is;
    private List<String> islandRequirements;

    public FileItemRecipe(File f) {
        load(f);
        RSStorage.register(Feature.ITEM_RECIPE, this);
    }

    public String getIdentifier() { return getYamlName(); }

    public int getSlot() {
        if(slot == -999) {
            slot = yml.getInt("settings.slot");
        }
        return slot;
    }
    public String getName() {
        if(name == null) {
            name = colorize(yml.getString("settings.name"));
        }
        return name;
    }

    public ItemStack getItem() {
        if(is == null) {
            is = RandomSkyAPI.INSTANCE.d(yml, "item");
        }
        return getClone(is);
    }
    public List<String> getIslandRequirements() {
        if(islandRequirements == null) {
            islandRequirements = yml.getStringList("requirements");
        }
        return islandRequirements;
    }
}
