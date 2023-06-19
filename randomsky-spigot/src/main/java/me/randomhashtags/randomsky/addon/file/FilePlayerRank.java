package me.randomhashtags.randomsky.addon.file;

import me.randomhashtags.randomsky.RandomSkyAPI;
import me.randomhashtags.randomsky.addon.PlayerRank;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSAddon;
import me.randomhashtags.randomsky.util.RSStorage;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class FilePlayerRank extends RSAddon implements PlayerRank {
    private int rankValue = -999;
    private ItemStack is;
    private String appearance;
    private List<String> attributes;

    public FilePlayerRank(File f) {
        load(f);
        RSStorage.register(Feature.PLAYER_RANK, this);
    }

    public String getIdentifier() { return getYamlName(); }

    public int getRankValue() {
        if(rankValue == -999) {
            rankValue = yml.getInt("rank value");
        }
        return rankValue;
    }
    public ItemStack getItem() {
        if(is == null) {
            is = RandomSkyAPI.INSTANCE.d(yml, "item");
        }
        return getClone(is);
    }
    public String getAppearance() {
        if(appearance == null) {
            appearance = colorize(yml.getString("appearance"));
        }
        return appearance;
    }
    public List<String> getAttributes() {
        if(attributes == null) {
            attributes = yml.getStringList("attributes");
        }
        return attributes;
    }
}
