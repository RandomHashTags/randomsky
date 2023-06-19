package me.randomhashtags.randomsky.addon.obj;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResourceNodeType {
    public static HashMap<String, ResourceNodeType> types;

    public String path;
    public List<String> lore;
    public ResourceNodeType(String path, List<String> lore) {
        if(types == null) {
            types = new HashMap<>();
        }
        this.path = path;
        final List<String> l = new ArrayList<>();
        for(String s : lore) l.add(ChatColor.translateAlternateColorCodes('&', s));
        this.lore = l;
        types.put(path, this);
    }
    public static void deleteAll() {
        types = null;
    }
}
