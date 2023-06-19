package me.randomhashtags.randomsky.attribute;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SetDroppedExp extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull Event event, HashMap<String, Entity> entities, String value, HashMap<String, String> valueReplacements) {
        switch (event.getEventName().toLowerCase().split("event")[0]) {
            case "entitydeath":
                int amount = (int) evaluate(replaceValue(entities, value, valueReplacements));
                ((EntityDeathEvent) event).setDroppedExp(amount);
                break;
            case "blockbreak":
                amount = (int) evaluate(replaceValue(entities, value, valueReplacements));
                ((BlockBreakEvent) event).setExpToDrop(amount);
                break;
            default:
                break;
        }
    }
}
