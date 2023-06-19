package me.randomhashtags.randomsky.attribute;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SetXp extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull Event event, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof Player) {
                final Player player = (Player) e;
                setTotalExperience(player, (int) evaluate(recipientValues.get(e).replace("xp", Integer.toString(getTotalExperience(player)))));
            }
        }
    }
}
