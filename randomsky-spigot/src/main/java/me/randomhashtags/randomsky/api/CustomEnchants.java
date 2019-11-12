package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.universal.UInventory;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class CustomEnchants extends RSFeature {
    private static CustomEnchants instance;
    public static CustomEnchants getCustomEnchants() {
        if(instance == null) instance = new CustomEnchants();
        return instance;
    }

    public YamlConfiguration config;
    private UInventory gemForgeExamine, gemForgeApply, gemForgeRemove;

    public void load() {
        save(null, "custom enchants.yml");
        config = YamlConfiguration.loadConfiguration(new File(randomsky.getDataFolder(), "custom enchants.yml"));
    }
    public void unload() {
        customenchantrarities = null;
        customenchantenabled = null;
        customenchantdisabled = null;
    }
}
