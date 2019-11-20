package me.randomhashtags.randomsky.addon.file;

import me.randomhashtags.randomsky.addon.ResourceNode;
import me.randomhashtags.randomsky.addon.island.IslandLevel;
import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSAddon;
import me.randomhashtags.randomsky.util.RSStorage;

import java.io.File;

public class FileResourceNode extends RSAddon implements ResourceNode {

    private IslandLevel requiredIslandLevel;
    private String nodeName, nodeType;

    public FileResourceNode(File f) {
        load(f);
        RSStorage.register(Feature.RESOURCE_NODE, this);
    }

    public IslandLevel getRequiredIslandLevel() {
        if(requiredIslandLevel == null) {
            final Identifiable i = RSStorage.get(Feature.ISLAND_LEVEL, Integer.toString(yml.getInt("settings.required island level")));
            if(i != null) {
                requiredIslandLevel = (IslandLevel) i;
            }
        }
        return requiredIslandLevel;
    }
    public String getNodeName() {
        if(nodeName == null) {
            nodeName = colorize(yml.getString("settings.node name"));
        }
        return nodeName;
    }
    public String getNodeType() {
        if(nodeType == null) {
            nodeType = colorize(yml.getString("settings.node {TYPE}"));
        }
        return nodeType;
    }
}
