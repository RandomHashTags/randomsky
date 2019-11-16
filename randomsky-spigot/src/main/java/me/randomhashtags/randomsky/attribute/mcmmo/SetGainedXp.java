package me.randomhashtags.randomsky.attribute.mcmmo;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import me.randomhashtags.randomsky.attribute.AbstractEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.HashMap;

public class SetGainedXp extends AbstractEventAttribute {
    @Override
    public void execute(Event event, HashMap<String, Entity> entities, String value, HashMap<String, String> valueReplacements) {
        if(event instanceof McMMOPlayerXpGainEvent) {
            final McMMOPlayerXpGainEvent e = (McMMOPlayerXpGainEvent) event;
            e.setRawXpGained((float) evaluate(replaceValue(entities, value, valueReplacements)));
        }
    }
}
