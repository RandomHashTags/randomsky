package me.randomhashtags.randomsky.addon.file;

import me.randomhashtags.randomsky.addon.PlayerSkill;
import me.randomhashtags.randomsky.addon.PlayerSkillLevel;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSAddon;
import me.randomhashtags.randomsky.util.RSStorage;
import me.randomhashtags.randomsky.universal.UInventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;

public class FilePlayerSkill extends RSAddon implements PlayerSkill {
    private String name;
    private ItemStack is;
    private HashMap<Integer, PlayerSkillLevel> levels;
    private UInventory uinv;

    public FilePlayerSkill(File f) {
        load(f);
        RSStorage.register(Feature.PLAYER_SKILL, this);
    }

    public String getIdentifier() { return getYamlName(); }

    public String getName() {
        if(name == null) {
            name = colorize(yml.getString("settings.name"));
        }
        return name;
    }
    public int getMaxLevel() { return yml.getInt("settings.max level"); }
    public ItemStack getItem() {
        if(is == null) is = api.d(yml, "item");
        return getClone(is);
    }
    public HashMap<Integer, PlayerSkillLevel> getLevels() {
        if(levels == null) {
            levels = new HashMap<>();
        }
        return levels;
    }
    public UInventory getUInventory() {
        if(uinv == null) {
            uinv = new UInventory(null, 9, colorize(yml.getString("settings.title")));
        }
        return uinv;
    }
}
