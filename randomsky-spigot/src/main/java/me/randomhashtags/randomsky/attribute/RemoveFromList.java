package me.randomhashtags.randomsky.attribute;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class RemoveFromList extends AbstractEventAttribute implements Listable {
    @Override
    public void execute(@NotNull Event event, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final UUID u = e.getUniqueId();
            if(list.containsKey(u)) {
                list.get(u).remove(recipientValues.get(e));
            }
        }
    }
}
