package me.randomhashtags.randomsky.addon.file;

import me.randomhashtags.randomsky.RandomSkyAPI;
import me.randomhashtags.randomsky.addon.alliance.AllianceUpgrade;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSAddon;
import me.randomhashtags.randomsky.util.RSStorage;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FileAllianceUpgrade extends RSAddon implements AllianceUpgrade {
    private int slot = -999;
    private ItemStack is;
    private HashMap<Integer, List<String>> cost, attributes;
    public FileAllianceUpgrade(File f) {
        load(f);
        RSStorage.register(Feature.ALLIANCE_UPGRADE, this);
    }

    public String getIdentifier() { return getYamlName(); }

    public int getSlot() {
        if(slot == -999) {
            slot = yml.getInt("gui.slot");
        }
        return slot;
    }
    public ItemStack getItem() {
        if(is == null) {
            is = RandomSkyAPI.INSTANCE.d(yml, "gui");
        }
        return getClone(is);
    }

    public int getMaxLevel() { return getCost().size(); }
    private Set<String> getSection() { return yml.getConfigurationSection("levels").getKeys(false); }
    public HashMap<Integer, List<String>> getCost() {
        if(cost == null) {
            cost = new HashMap<>();
            for(String s : getSection()) {
                cost.put(Integer.parseInt(s), yml.getStringList("levels." + s + ".cost"));
            }
        }
        return cost;
    }
    public HashMap<Integer, List<String>> getAttributes() {
        if(attributes == null) {
            attributes = new HashMap<>();
            for(String s : getSection()) {
                attributes.put(Integer.parseInt(s), yml.getStringList("levels." + s + ".attributes"));
            }
        }
        return attributes;
    }
}
