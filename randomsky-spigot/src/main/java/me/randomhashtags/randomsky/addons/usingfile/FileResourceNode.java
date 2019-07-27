package me.randomhashtags.randomsky.addons.usingfile;

import me.randomhashtags.randomsky.addons.ResourceNode;
import org.bukkit.ChatColor;

import java.io.File;

public class FileResourceNode extends ResourceNode {

    public FileResourceNode(File f) {
        load(f);
        addResourceNode(this);
    }
    public String getIdentifier() { return getYamlName(); }

    public String getNodeName() { return ChatColor.translateAlternateColorCodes('&', yml.getString("settings.node name")); }
    public String getNodeType() { return ChatColor.translateAlternateColorCodes('&', yml.getString("settings.node {TYPE}")); }
}
