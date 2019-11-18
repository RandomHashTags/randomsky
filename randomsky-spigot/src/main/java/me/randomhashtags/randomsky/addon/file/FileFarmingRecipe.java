package me.randomhashtags.randomsky.addon.file;

import me.randomhashtags.randomsky.addon.FarmingRecipe;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSAddon;
import me.randomhashtags.randomsky.util.RSStorage;
import me.randomhashtags.randomsky.util.universal.UMaterial;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class FileFarmingRecipe extends RSAddon implements FarmingRecipe {
    private UMaterial unlocks;
    private String name;
    private ItemStack is;

    public FileFarmingRecipe(File f) {
        load(f);
        RSStorage.register(Feature.FARMING_RECIPE, this);
    }

    public String getIdentifier() { return getYamlName(); }

    public UMaterial getUnlocks() {
        if(unlocks == null) {
            unlocks = UMaterial.match(yml.getString("unlocks"));
        }
        return unlocks;
    }
    public String getName() {
        if(name == null) {
            name = colorize(yml.getString("name"));
        }
        return name;
    }
    public ItemStack getItem() {
        if(is == null) {
            is = api.d(yml, "item");
        }
        return getClone(is);
    }
}
