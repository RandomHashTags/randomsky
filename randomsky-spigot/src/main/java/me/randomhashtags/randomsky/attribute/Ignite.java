package me.randomhashtags.randomsky.attribute;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;

public class Ignite extends AbstractEventAttribute {
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            ignite(e, recipientValues.get(e));
        }
    }
    private void ignite(Entity entity, String value) {
        entity.setFireTicks((int) evaluate(value));
    }
}
