package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.Identifiable;
import org.bukkit.event.Event;

public interface EventAttributeListener extends Identifiable {
    void called(Event event);
}
