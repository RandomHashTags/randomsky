package me.randomhashtags.randomsky.attribute;

import me.randomhashtags.randomsky.event.DamageEvent;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SetDamage extends AbstractEventAttribute implements EventEntities {
    @Override
    public void execute(@NotNull Event event, String value, HashMap<String, String> valueReplacements) {
        if(event instanceof EntityDamageEvent) {
            final EntityDamageEvent e = (EntityDamageEvent) event;
            e.setDamage(evaluate(replaceValue(getEntities(e), value, valueReplacements)));
        } else if(event instanceof DamageEvent) {
            final DamageEvent e = (DamageEvent) event;
            e.setDamage(evaluate(replaceValue(getEntities(e), value, valueReplacements)));
        }
    }
}
