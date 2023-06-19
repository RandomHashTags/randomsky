package me.randomhashtags.randomsky.attribute;

import me.randomhashtags.randompackage.event.kit.KitPreClaimEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SetLevelupChance extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull Event event, HashMap<String, Entity> entities, String value, HashMap<String, String> valueReplacements) {
        if(event instanceof KitPreClaimEvent) {
            final KitPreClaimEvent ev = (KitPreClaimEvent) event;
            ev.setLevelupChance((int) evaluate(replaceValue(entities, value, valueReplacements)));
        }
    }
}
