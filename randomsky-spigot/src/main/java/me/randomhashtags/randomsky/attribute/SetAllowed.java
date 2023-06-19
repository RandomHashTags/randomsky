package me.randomhashtags.randomsky.attribute;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

public class SetAllowed extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull Event event, String value) {
        if(event instanceof PlayerCommandPreprocessEvent) {
            ((PlayerCommandPreprocessEvent) event).setCancelled(Boolean.parseBoolean(value));
        }
    }
}
