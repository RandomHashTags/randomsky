package me.randomhashtags.randomsky.addon.obj;

import me.randomhashtags.randomsky.addon.ResourceNode;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class MiningSkillObj {
    public static HashMap<String, MiningSkillObj> paths;
    public static HashMap<Integer, MiningSkillObj> slots;

    public String path;
    public int slot;
    public ResourceNode tracks;
    private ItemStack display;
    public MiningSkillObj(String path, int slot, ResourceNode tracks, ItemStack display) {
        if(paths == null) {
            paths = new HashMap<>();
            slots = new HashMap<>();
        }
        this.path = path;
        this.slot = slot;
        this.tracks = tracks;
        this.display = display;
        paths.put(path, this);
        slots.put(slot, this);
    }
    public ItemStack display() { return display.clone(); }

    public static void deleteAll() {
        paths = null;
        slots = null;
    }
}
