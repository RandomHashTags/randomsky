package me.randomhashtags.randomsky.attribute;

import me.randomhashtags.randompackage.event.PlayerTeleportDelayEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SetDelay extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull Event event, HashMap<String, Entity> entities, String value, HashMap<String, String> valueReplacements) {
        if(event instanceof PlayerTeleportDelayEvent) {
            final PlayerTeleportDelayEvent e = (PlayerTeleportDelayEvent) event;
            e.setDelay(evaluate(replaceValue(entities, value, valueReplacements)));
        }
    }
}
