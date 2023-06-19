package me.randomhashtags.randomsky.attribute;

import me.randomhashtags.randompackage.event.mob.MobStackDepleteEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class DepleteStackSize extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull Event event, String value) {
        if(event instanceof MobStackDepleteEvent) {
            final MobStackDepleteEvent e = (MobStackDepleteEvent) event;
            e.amount = (int) evaluate(value);
        }
    }
}
