package me.randomhashtags.randomsky.attribute;

import me.randomhashtags.randomsky.addon.EventCondition;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSStorage;
import me.randomhashtags.randomsky.universal.UVersion;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;

public abstract class AbstractEventCondition extends UVersion implements EventCondition {
    public String getIdentifier() {
        final String[] n = getClass().getName().split("\\.");
        return n[n.length-1].toUpperCase();
    }
    public void load() {
        RSStorage.register(Feature.EVENT_CONDITION, this);
    }
    public void unload() {
        RSStorage.unregister(Feature.EVENT_CONDITION, this);
    }

    public boolean check(String value) { return true; }
    public boolean check(Event event) { return true; }
    public boolean check(Event event, Entity entity) { return true; }
    public boolean check(Event event, String value) { return true; }
    public boolean check(Entity entity, String value) { return true; }
    public boolean check(String entity, HashMap<String, Entity> entities, String value) { return true; }
}
