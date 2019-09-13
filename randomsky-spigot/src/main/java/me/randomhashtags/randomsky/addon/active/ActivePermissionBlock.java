package me.randomhashtags.randomsky.addon.active;

import me.randomhashtags.randomsky.addon.PermissionBlock;
import me.randomhashtags.randomsky.util.obj.PolyBoundary;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ActivePermissionBlock {
    private UUID placer, uuid;
    private List<UUID> members = new ArrayList<>();
    private Location location;
    private PolyBoundary boundary;
    private PermissionBlock type;
    private HashMap<String, Boolean> settings;
    public ActivePermissionBlock(UUID placer, Location location, PermissionBlock type) {
        this(placer, UUID.randomUUID(), location, type, new ArrayList<>(), new HashMap<>());
    }
    public ActivePermissionBlock(UUID placer, UUID uuid, Location location, PermissionBlock type, List<UUID> members, HashMap<String, Boolean> settings) {
        this.placer = placer;
        this.uuid = uuid;
        this.location = location;
        this.type = type;
        this.members.addAll(members);
        this.settings = settings;
        this.boundary = new PolyBoundary(location, type.getRadius()-1);
    }
    public ActivePermissionBlock delete() {
        this.location.getWorld().getBlockAt(location).setType(Material.AIR);
        return this;
    }
    public UUID getPlacer() { return placer; }
    public UUID getUUID() { return uuid; }
    public List<UUID> getMembers() { return members; }
    public PermissionBlock getType() { return type; }
    public Location getLocation() { return location; }
    public PolyBoundary getBoundary() { return boundary; }

    public HashMap<String, Boolean> getSettings() { return settings; }
    public boolean getSetting(String identifier) { return settings.getOrDefault(identifier, true); }
    public void setSetting(String identifier, boolean value) { settings.put(identifier, value); }
}
