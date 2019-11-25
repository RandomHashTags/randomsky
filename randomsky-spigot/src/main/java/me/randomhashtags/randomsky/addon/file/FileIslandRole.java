package me.randomhashtags.randomsky.addon.file;

import me.randomhashtags.randomsky.addon.island.IslandRole;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSAddon;
import me.randomhashtags.randomsky.util.RSStorage;

import java.io.File;
import java.util.List;

public class FileIslandRole extends RSAddon implements IslandRole {
    private String rank, name;
    private List<String> lore, perms;
    public FileIslandRole(File f) {
        load(f);
        RSStorage.register(Feature.ISLAND_ROLE, this);
    }

    public String getIdentifier() { return getYamlName(); }

    public String getRank() {
        if(rank == null) {
            rank = colorize(yml.getString("rank"));
        }
        return rank;
    }
    public String getName() {
        if(name == null) {
            name = colorize(yml.getString("name"));
        }
        return name;
    }
    public List<String> getLore() {
        if(lore == null) {
            lore = colorizeListString(yml.getStringList("lore"));
        }
        return lore;
    }
    public List<String> getPermissions() {
        if(perms == null) {
            perms = colorizeListString(yml.getStringList("permissions"));
        }
        return perms;
    }
}
