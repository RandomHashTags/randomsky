package me.randomhashtags.randomsky.addon.active;

import org.bukkit.Location;

public class Home {
    private String name;
    private Location location;
    public Home(String name, Location location) {
        this.name = name;
        this.location = location;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Location getLocation() { return location; }
    public void setLocation(Location l) { location = l; }
}
