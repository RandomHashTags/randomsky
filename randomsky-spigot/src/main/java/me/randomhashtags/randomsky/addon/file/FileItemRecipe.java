package me.randomhashtags.randomsky.addon.file;

import me.randomhashtags.randomsky.dev.ItemRecipe;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSAddon;
import me.randomhashtags.randomsky.util.RSStorage;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class FileItemRecipe extends RSAddon implements ItemRecipe {
    private ItemStack is;

    public FileItemRecipe(File f) {
        load(f);
        RSStorage.register(Feature.ITEM_RECIPE, this);
    }

    public String getIdentifier() { return getYamlName(); }
    public ItemStack getItem() {
        if(is == null) {
            is = api.d(yml, "item");
        }
        return getClone(is);
    }
}
