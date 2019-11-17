package me.randomhashtags.randomsky.addon.file;

import me.randomhashtags.randomsky.addon.bot.AutoBot;
import me.randomhashtags.randomsky.addon.bot.AutoBotUpgrade;
import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSAddon;
import me.randomhashtags.randomsky.util.RSStorage;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class FileAutoBot extends RSAddon implements AutoBot {
    private String name, type;
    private ItemStack is;
    private HashMap<Integer, AutoBotUpgrade> upgrades;
    private List<String> attributes;
    public FileAutoBot(File f) {
        load(f);
        RSStorage.register(Feature.AUTO_BOT, this);
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
    public HashMap<Integer, AutoBotUpgrade> getUpgrades() {
        if(upgrades == null) {
            upgrades = new HashMap<>();
            for(String s : yml.getConfigurationSection("upgrades").getKeys(false)) {
                final Identifiable i = RSStorage.get(Feature.AUTO_BOT_UPGRADE, s);
                if(i != null) {
                    upgrades.put(yml.getInt("upgrades." + s + ".slot"), (AutoBotUpgrade) i);
                }
            }
        }
        return upgrades;
    }
    public List<String> getAttributes() {
        if(attributes == null) {
            attributes = yml.getStringList("attributes");
        }
        return attributes;
    }
}
