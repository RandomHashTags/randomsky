package me.randomhashtags.randomsky.attribute;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class SetCancelled extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull Event event, String value) {
        if(event instanceof Cancellable) {
            ((Cancellable) event).setCancelled(Boolean.parseBoolean(value));
        }
    }
}
