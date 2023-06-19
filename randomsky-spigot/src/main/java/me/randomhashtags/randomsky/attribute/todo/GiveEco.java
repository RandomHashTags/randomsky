package me.randomhashtags.randomsky.attribute.todo;
import me.randomhashtags.randomsky.attribute.AbstractEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class GiveEco extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull Event event, HashMap<Entity, String> recipientValues) {
        // TODO: Support more than 1 economy plugin & finish this attribute
    }
}
