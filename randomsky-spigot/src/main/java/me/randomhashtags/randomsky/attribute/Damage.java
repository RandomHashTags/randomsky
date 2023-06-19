package me.randomhashtags.randomsky.attribute;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Damage extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull Event event, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final LivingEntity l = e instanceof LivingEntity ? (LivingEntity) e : null;
            if(l != null) {
                final String value = recipientValues.get(e);
                if(value != null) {
                    l.damage(evaluate(value));
                }
            }
        }
    }
}
