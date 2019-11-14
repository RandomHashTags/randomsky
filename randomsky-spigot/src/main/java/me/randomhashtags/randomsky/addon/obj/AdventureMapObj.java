package me.randomhashtags.randomsky.addon.obj;

import me.randomhashtags.randomsky.addon.adventure.AdventureMap;
import me.randomhashtags.randomsky.util.universal.UVersionable;
import org.bukkit.inventory.ItemStack;

public class AdventureMapObj implements AdventureMap, UVersionable {
    private String identifier, foundIn;
    private ItemStack is;
    public AdventureMapObj(String identifier, ItemStack is, String foundIn) {
        this.identifier = identifier;
        this.is = is;
        this.foundIn = foundIn;
    }
    public String getIdentifier() { return identifier; }
    public ItemStack getItem() { return getClone(is); }
    public String getFoundIn() { return foundIn; }
}
