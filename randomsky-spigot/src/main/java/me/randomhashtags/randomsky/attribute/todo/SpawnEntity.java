package me.randomhashtags.randomsky.attribute.todo;

import me.randomhashtags.randomsky.attribute.AbstractEventAttribute;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SpawnEntity extends AbstractEventAttribute {
    // TODO: finish this attribute
    @Override
    public void execute(@NotNull Event event, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            spawnentity(e, recipientValues.get(e));
        }
    }
    @Override
    public void executeAt(HashMap<Location, String> locations) {
        for(Location l : locations.keySet()) {
            spawnentity(l, locations.get(l));
        }
    }
    private void spawnentity(Object o, String value) {
        final String[] values = value.split(":");
    }
}
