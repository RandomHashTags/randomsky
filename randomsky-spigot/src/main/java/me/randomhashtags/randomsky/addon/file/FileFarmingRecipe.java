package me.randomhashtags.randomsky.addon.file;

import me.randomhashtags.randomsky.addon.FarmingRecipe;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSAddon;
import me.randomhashtags.randomsky.util.RSStorage;
import me.randomhashtags.randomsky.util.universal.UMaterial;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.math.BigDecimal;

public class FileFarmingRecipe extends RSAddon implements FarmingRecipe {
    private UMaterial unlocks;
    private String recipeName, type;
    private BigDecimal dailyLimit, completion;
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
    public String getRecipeName() {
        if(recipeName == null) {
            recipeName = colorize(yml.getString("recipe name"));
        }
        return recipeName;
    }
    public BigDecimal getDailyLimit() {
        if(dailyLimit == null) {
            dailyLimit = BigDecimal.valueOf(yml.getDouble("settings.daily limit", -1));
        }
        return dailyLimit;
    }
    public BigDecimal getCompletion() {
        if(completion == null) {
            completion = BigDecimal.valueOf(yml.getDouble("settings.completion", -1));
        }
        return completion;
    }
    public String getType() {
        if(type == null) {
            type = colorize(yml.getString("settings.type"));
        }
        return type;
    }
    public ItemStack getItem() {
        if(is == null) {
            is = api.d(yml, "item");
        }
        return getClone(is);
    }
}
