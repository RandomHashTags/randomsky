package me.randomhashtags.randomsky.addon.file;

import me.randomhashtags.randomsky.addon.FarmingRecipe;
import me.randomhashtags.randomsky.addon.island.skill.FarmingSkill;
import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSAddon;
import me.randomhashtags.randomsky.util.RSStorage;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.math.BigDecimal;

public class FileFarmingSkill extends RSAddon implements FarmingSkill {
    private int slot = -999;
    private String requiredSkill, type;
    private BigDecimal dailyLimit, completion;
    private ItemStack is;
    private FarmingRecipe recipe;

    public FileFarmingSkill(File f) {
        load(f);
        RSStorage.register(Feature.ISLAND_SKILL, this);
    }
    public String getIdentifier() { return "FARMING_" + getYamlName(); }

    public int getSlot() {
        if(slot == -999) {
            slot = yml.getInt("settings.slot");
        }
        return slot;
    }
    public String getRequiredCompletedSkill() {
        if(requiredSkill == null) {
            requiredSkill = yml.getString("settings.required skill");
        }
        return requiredSkill;
    }
    public String getType() {
        if(type == null) {
            type = colorize(yml.getString("settings.type"));
        }
        return type;
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
    public ItemStack getItem() {
        if(is == null) {
            is = api.d(yml, "item");
        }
        return getClone(is);
    }
    public FarmingRecipe getRequiredRecipe() {
        if(recipe == null) {
            final Identifiable i = RSStorage.get(Feature.FARMING_RECIPE, yml.getString("settings.required recipe"));
            if(i != null) {
                recipe = (FarmingRecipe) i;
            }
        }
        return recipe;
    }
}
