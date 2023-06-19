package me.randomhashtags.randomsky.attribute;

import me.randomhashtags.randompackage.event.lootbag.LootbagClaimEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SetRewardSize extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull Event event, HashMap<String, Entity> entities, String value, HashMap<String, String> valueReplacements) {
        if(event instanceof LootbagClaimEvent) {
            final LootbagClaimEvent e = (LootbagClaimEvent) event;
            e.setRewardSize((int) evaluate(replaceValue(entities, value, valueReplacements)));
        }
    }
}
