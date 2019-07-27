package me.randomhashtags.randomsky.utils;

import me.randomhashtags.randomsky.addons.Island;
import me.randomhashtags.randomsky.addons.ResourceNode;
import me.randomhashtags.randomsky.utils.universal.UVersion;

import java.util.LinkedHashMap;
import java.util.UUID;

public abstract class RSStorage extends UVersion {

    protected static LinkedHashMap<UUID, Island> islands;
    protected static LinkedHashMap<String, ResourceNode> resourceNodes;

    public Island getIsland(UUID uuid) {
        return islands != null ? islands.get(uuid) : null;
    }
    public void addIsland(Island island) {
        if(islands == null) islands = new LinkedHashMap<>();
        final UUID u = island.getUUID();
        if(!islands.containsKey(u)) islands.put(u, island);
    }

    public ResourceNode getResourceNode(String identifier) {
        return resourceNodes != null ? resourceNodes.get(identifier) : null;
    }
    public void addResourceNode(ResourceNode node) {
        if(resourceNodes == null) resourceNodes = new LinkedHashMap<>();
        resourceNodes.put(node.getIdentifier(), node);
    }

}
