package me.randomhashtags.randomsky.addon.file;

import me.randomhashtags.randomsky.addon.adventure.Adventure;
import me.randomhashtags.randomsky.addon.adventure.AdventureMap;
import me.randomhashtags.randomsky.addon.obj.AdventureMapObj;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSAddon;
import me.randomhashtags.randomsky.util.RSStorage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class FileAdventure extends RSAddon implements Adventure {
    private ItemStack is;
    private String name;
    private float teleportDelay = -999;
    private AdventureMap map;
    private List<String> teleportLocations, blacklistedItems, chestLocations;

    public FileAdventure(File f) {
        load(f);
        RSStorage.register(Feature.ADVENTURE, this);
    }

    public String getIdentifier() { return getYamlName(); }

    public Location getCenter() { return toLocation(yml.getString("center")); }
    public String getName() {
        if(name == null) name = ChatColor.translateAlternateColorCodes('&', yml.getString("name"));
        return name;
    }
    public int getMaxPlayers() { return yml.getInt("max players"); }
    public float getTeleportDelay() {
        if(teleportDelay == -999) teleportDelay = (float) yml.getDouble("teleport delay");
        return teleportDelay;
    }
    public List<String> getTeleportLocations() {
        if(teleportLocations == null) teleportLocations = yml.getStringList("teleport locations");
        return teleportLocations;
    }
    public AdventureMap getRequiredMap() {
        if(map == null && yml.get("map") != null) {
            final AdventureMap map = new AdventureMapObj(getIdentifier(), api.d(yml, "map"), yml.getString("map.found in"));
            RSStorage.register(Feature.ADVENTURE_MAP, map);
            this.map = map;
        }
        return map;
    }

    public int getSlot() { return yml.getInt("gui.slot"); }
    public ItemStack getItem() {
        if(is == null) is = api.d(yml, "gui");
        return getClone(is);
    }

    public List<String> getBlacklistedItems() {
        if(blacklistedItems == null) blacklistedItems = yml.getStringList("blacklisted items");
        return blacklistedItems;
    }

    public List<String> getChestLocations() {
        if(chestLocations == null) chestLocations = yml.getStringList("chest locations");
        return chestLocations;
    }
}
