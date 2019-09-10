package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.util.universal.UMaterial;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class ResourceNode extends Itemable {
    public abstract String getNodeName();
    public abstract String getNodeType();
    public abstract String getRequiredNode();
    public abstract double getValue();
    public abstract long getRespawnTime();
    public abstract UMaterial getHarvestBlock();
    public abstract UMaterial getNodeBlock();
    public abstract List<String> getLoot();

    public ItemStack getItem() {
        return getItem(getRespawnTime());
    }
    public ItemStack getItem(long respawnTime) {
        final ItemStack i = getItem();
        final ItemMeta itemMeta = i.getItemMeta();
        final List<String> l = new ArrayList<>();
        for(String s : itemMeta.getLore()) {
            l.add(s.replace("{RESPAWN}", api.getRemainingTime(respawnTime*1000)));
        }
        itemMeta.setLore(l);
        i.setItemMeta(itemMeta);
        return i;
    }


    public static ResourceNode valueOf(ItemStack is) {
        if(resourceNodes != null && is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
            final List<String> l = is.getItemMeta().getLore(), lore = new ArrayList<>();
            final int S = l.size();
            for(ResourceNode n : resourceNodes.values()) {
                final ItemStack I = n.getItem();
                final ItemMeta itemMeta = I.getItemMeta();
                final List<String> L = I.getItemMeta().getLore();
                if(L.size() == S) {
                    int i = 0;
                    for(String s : L) {
                        if(s.contains("{RESPAWN}")) {
                            s = l.get(i);
                        }
                        lore.add(s);
                        i++;
                    }
                    itemMeta.setLore(lore); lore.clear();
                    I.setItemMeta(itemMeta);
                    if(is.isSimilar(I)) {
                        return n;
                    }
                }
            }
        }
        return null;
    }
    public static ResourceNode getNextLevel(ResourceNode current) {
        if(resourceNodes != null) {
            final String s = current.getIdentifier();
            for(ResourceNode n : resourceNodes.values()) {
                final String r = n.getRequiredNode();
                if(r != null && r.contains(s)) {
                    return n;
                }
            }
        }
        return null;
    }
}
