package me.randomhashtags.randomsky.addon.file;

import me.randomhashtags.randomsky.RandomSkyAPI;
import me.randomhashtags.randomsky.addon.island.IslandOrigin;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSAddon;
import me.randomhashtags.randomsky.util.RSStorage;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

import static java.io.File.separator;

public class FileIslandOrigin extends RSAddon implements IslandOrigin {
    private ItemStack is;
    private String name;
    private File schematic;
    private List<String> attributes;

    public FileIslandOrigin(File f) {
        load(f);
        RSStorage.register(Feature.ISLAND_ORIGIN, this);
    }

    public String getIdentifier() { return getYamlName(); }

    public ItemStack getItem() {
        if(is == null) is = RandomSkyAPI.INSTANCE.d(yml, "item");
        return getClone(is);
    }
    public String getName() {
        if(name == null) name = colorize(yml.getString("settings.name"));
        return name;
    }
    public File getSchematic() {
        if(schematic == null) schematic = new File(DATA_FOLDER + separator + "origins", yml.getString("settings.schematic") + ".schematic");
        return schematic;
    }
    public int getSlot() { return yml.getInt("settings.slot"); }
    public List<String> getAttributes() {
        if(attributes == null) attributes = yml.getStringList("attributes");
        return attributes;
    }
}
