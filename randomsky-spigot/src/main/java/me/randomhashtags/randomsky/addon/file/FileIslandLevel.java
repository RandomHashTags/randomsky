package me.randomhashtags.randomsky.addon.file;

import me.randomhashtags.randomsky.addon.island.IslandLevel;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSAddon;
import me.randomhashtags.randomsky.util.RSStorage;

import java.io.File;
import java.util.List;

public class FileIslandLevel extends RSAddon implements IslandLevel {

    private int level = -999, slot = -999;
    private List<String> cost, attributes;

    public FileIslandLevel(File f) {
        load(f);
        RSStorage.register(Feature.ISLAND_LEVEL, this);
    }

    public String getIdentifier() { return getYamlName(); }

    public IslandLevel getRequiredIslandLevel() {
        return null;
    }
    public int getSlot() {
        if(slot == -999) {
            slot = yml.getInt("settings.slot");
        }
        return slot;
    }
    public int getLevel() {
        if(level == -999) {
            level = Integer.parseInt(getIdentifier());
        }
        return level;
    }
    public List<String> getCost() {
        if(cost == null) {
            cost = yml.getStringList("cost");
        }
        return cost;
    }
    public List<String> getAttributes() {
        if(attributes == null) {
            attributes = yml.getStringList("attributes");
        }
        return attributes;
    }
}
