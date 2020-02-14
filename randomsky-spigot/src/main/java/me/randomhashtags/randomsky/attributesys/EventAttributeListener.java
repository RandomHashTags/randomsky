package me.randomhashtags.randomsky.attributesys;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randomsky.addon.util.Identifiable;
import org.bukkit.event.Event;

public interface EventAttributeListener extends Identifiable {
    void called(@NotNull Event event);
}
