package me.randomhashtags.randomsky.addon.file;

import me.randomhashtags.randomsky.RandomSkyAPI;
import me.randomhashtags.randomsky.addon.FarmingLimitIncrease;
import me.randomhashtags.randomsky.addon.FarmingRecipe;
import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSAddon;
import me.randomhashtags.randomsky.util.RSStorage;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileFarmingLimitIncreaser extends RSAddon implements FarmingLimitIncrease {
    private String[] percents;
    private long duration = -1;
    private ItemStack is;
    private List<FarmingRecipe> appliesto;
    public FileFarmingLimitIncreaser(File f) {
        load(f);
        RSStorage.register(Feature.FARMING_LIMIT_INCREASE, this);
    }

    public String getIdentifier() { return getYamlName(); }

    private String[] getPercents() {
        if(percents == null) {
            percents = yml.getString("settings.percents").split(";");
        }
        return percents;
    }
    public int getMinPercent() { return Integer.parseInt(getPercents()[0]); }
    public int getMaxPercent() { return Integer.parseInt(getPercents()[1]); }
    public long getDuration() {
        if(duration == -1) {
            duration = yml.getLong("settings.duration");
        }
        return duration;
    }
    public ItemStack getItem() {
        if(is == null) {
            is = RandomSkyAPI.INSTANCE.d(yml, "item");
        }
        return getClone(is);
    }
    public ItemStack getItem(int percent) {
        final String p = Integer.toString(percent);
        final ItemStack is = getItem();
        final ItemMeta m = is.getItemMeta();
        final List<String> l = new ArrayList<>();
        for(String s : m.getLore()) {
            l.add(s.replace("{PERCENT}", p));
        }
        m.setLore(l);
        is.setItemMeta(m);
        return is;
    }
    public List<FarmingRecipe> getAppliesTo() {
        if(appliesto == null) {
            appliesto = new ArrayList<>();
            for(String s : yml.getStringList("applies to")) {
                final Identifiable i = RSStorage.get(Feature.FARMING_RECIPE, s);
                if(i != null) {
                    appliesto.add((FarmingRecipe) i);
                }
            }
        }
        return appliesto;
    }
}
