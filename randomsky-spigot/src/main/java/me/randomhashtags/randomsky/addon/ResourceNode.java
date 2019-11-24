package me.randomhashtags.randomsky.addon;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.addon.util.RequiredIslandLevel;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSStorage;
import me.randomhashtags.randomsky.util.universal.UMaterial;
import me.randomhashtags.randomsky.util.universal.UVersionable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface ResourceNode extends Itemable, RequiredIslandLevel, UVersionable {
    String getNodeName();
    String getNodeType();
    HashMap<String, BigDecimal> getRequiredNodes();
    double getValue();
    long getRespawnTime();
    UMaterial getHarvestBlock();
    UMaterial getNodeBlock();
    List<String> getLoot();

    default ItemStack getItem() {
        return getItem(getRespawnTime());
    }
    default ItemStack getItem(long respawnTime) {
        final ItemStack i = getItem();
        final ItemMeta itemMeta = i.getItemMeta();
        final List<String> l = new ArrayList<>();
        for(String s : itemMeta.getLore()) {
            l.add(s.replace("{RESPAWN}", getRemainingTime(respawnTime*1000)));
        }
        itemMeta.setLore(l);
        i.setItemMeta(itemMeta);
        return i;
    }


    static ResourceNode valueOf(@NotNull ItemStack is) {
        if(resourceNodes != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
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
    static ResourceNode getNextLevel(@NotNull ResourceNode current) {
        final HashMap<String, BigDecimal> required = current.getRequiredNodes();
        if(required != null && !required.isEmpty()) {
            final String id = current.getIdentifier();
            for(String s : required.keySet()) {
                final Identifiable i = RSStorage.get(Feature.RESOURCE_NODE, s);
                if(i != null) {
                    final ResourceNode next = (ResourceNode) i;
                    final HashMap<String, BigDecimal> requiredNodes = next.getRequiredNodes();
                    if(requiredNodes != null && requiredNodes.containsKey(id)) {
                        return next;
                    }
                }
            }
        }
        return null;
    }
}
