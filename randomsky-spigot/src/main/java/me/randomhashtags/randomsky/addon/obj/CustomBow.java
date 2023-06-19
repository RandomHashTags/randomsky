package me.randomhashtags.randomsky.addon.obj;

import me.randomhashtags.randomsky.universal.UMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class CustomBow {
    public static HashMap<String, CustomBow> paths;
    public final String path, name;
    public final List<String> lore;
    public final List<UMaterial> doesntWorkAgainst;
    public CustomBow(String path, String name, List<String> lore, List<UMaterial> doesntWorkAgainst) {
        if(paths == null) {
            paths = new HashMap<>();
        }
        this.path = path;
        this.name = name;
        this.lore = lore;
        this.doesntWorkAgainst = doesntWorkAgainst;
        paths.put(path, this);
    }
    public ItemStack item() {
        final ItemStack s = new ItemStack(Material.BOW);
        final ItemMeta m = s.getItemMeta();
        m.setDisplayName(name);
        m.setLore(lore);
        s.setItemMeta(m);
        return s;
    }
    public static void deleteAll() {
        paths = null;
    }
}
