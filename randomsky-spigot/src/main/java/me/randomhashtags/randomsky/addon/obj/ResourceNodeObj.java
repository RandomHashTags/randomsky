package me.randomhashtags.randomsky.addon.obj;

import me.randomhashtags.randomsky.RandomSkyAPI;
import me.randomhashtags.randomsky.addon.island.IslandLevel;
import me.randomhashtags.randomsky.universal.UMaterial;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResourceNodeObj {
    public static HashMap<String, ResourceNodeObj> paths;

    public String path, nodeName, nodeTYPE, requiredNode;
    public ResourceNodeType node;
    public IslandLevel requiredLevel;
    public long respawnTime;
    public double value;
    private ItemStack item;
    public List<String> loot;
    public UMaterial harvestBlock, nodeBlock;
    public int completion;

    public ResourceNodeObj(String path, ResourceNodeType node, IslandLevel requiredLevel, long respawnTime, double value, UMaterial harvestBlock, UMaterial nodeBlock, String nodeName, String nodeTYPE, String requiredNode, int completion, ItemStack item, List<String> loot) {
        if(paths == null) {
            paths = new HashMap<>();
        }
        this.path = path;
        this.node = node;
        this.requiredLevel = requiredLevel;
        this.respawnTime = respawnTime;
        this.value = value;
        this.harvestBlock = harvestBlock;
        this.nodeBlock = nodeBlock;
        this.nodeName = nodeName;
        this.nodeTYPE = nodeTYPE;
        this.requiredNode = requiredNode;
        this.completion = completion;
        this.item = item;
        this.loot = loot;
        paths.put(path, this);
    }
    public ItemStack item() { return item(respawnTime); }
    public ItemStack item(long respawnTime) {
        final ItemStack i = item.clone();
        final ItemMeta itemMeta = i.getItemMeta();
        final List<String> l = new ArrayList<>();
        for(String s : itemMeta.getLore()) {
            l.add(s.replace("{RESPAWN}", RandomSkyAPI.INSTANCE.getRemainingTime(respawnTime*1000)));
        }
        itemMeta.setLore(l);
        i.setItemMeta(itemMeta);
        return i;
    }
    public static ResourceNodeObj valueOf(ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
            final List<String> l = is.getItemMeta().getLore(), lore = new ArrayList<>();
            final int S = l.size();
            for(ResourceNodeObj n : paths.values()) {
                final ItemStack I = n.item.clone();
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

    public static ResourceNodeObj getNextLevel(ResourceNodeObj current) {
        final String s = current.path;
        for(ResourceNodeObj n : paths.values()) {
            if(n.requiredNode != null && n.requiredNode.contains(s)) {
                return n;
            }
        }
        return null;
    }
    public static void deleteAll() {
        paths = null;
    }
}
