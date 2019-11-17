package me.randomhashtags.randomsky.addon.file;

import me.randomhashtags.randomsky.addon.bot.AutoBotUpgrade;
import me.randomhashtags.randomsky.addon.obj.AutoBotUpgradeInfo;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSAddon;
import me.randomhashtags.randomsky.util.RSStorage;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class FileAutoBotUpgrade extends RSAddon implements AutoBotUpgrade {
    private String name, type;
    private ItemStack is;
    private AutoBotUpgradeInfo upgrades;
    public FileAutoBotUpgrade(File f) {
        load(f);
        RSStorage.register(Feature.AUTO_BOT_UPGRADE, this);
    }

    public String getIdentifier() { return getYamlName(); }

    public String getName() {
        if(name == null) {
            name = colorize(yml.getString("name"));
        }
        return name;
    }
    public String getType() {
        if(type == null) {
            type = colorize(yml.getString("type"));
        }
        return type;
    }

    public ItemStack getItem() {
        if(is == null) {
            is = api.d(yml, "item");
        }
        return getClone(is);
    }
    public AutoBotUpgradeInfo getUpgrades() {
        if(upgrades == null) {
            upgrades = new AutoBotUpgradeInfo(yml);
        }
        return upgrades;
    }
}
