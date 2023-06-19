package me.randomhashtags.randomsky.addon.obj;

import me.randomhashtags.randomsky.addon.ResourceType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ResourceObj {
    public static List<ResourceObj> resources;
    private ResourceType type;
    private String path;
    private ItemStack item;
    private ResourceNodeType node;
    public ResourceObj(ResourceType type, String path, ItemStack item) {
        this(type, path, item,null);
    }
    public ResourceObj(ResourceType type, String path, ItemStack item, ResourceNodeType node) {
        if(resources == null) {
            resources = new ArrayList<>();
        }
        this.type = type;
        this.path = path;
        this.item = item;
        this.node = node;
        resources.add(this);
    }
    public ResourceType getType() { return type; }
    public String getPath() { return path; }
    public ItemStack item() { return item.clone(); }
    public ResourceNodeType getNode() { return node; }

    public static ResourceObj valueOf(String path, ResourceType type) {
        if(resources != null) {
            for(ResourceObj r : resources) {
                if(r.path.equals(path) && r.type.equals(type)) {
                    return r;
                }
            }
        }
        return null;
    }
    public static void deleteAll() {
        resources = null;
    }
}