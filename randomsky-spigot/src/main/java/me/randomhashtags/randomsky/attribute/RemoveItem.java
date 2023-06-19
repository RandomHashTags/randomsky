package me.randomhashtags.randomsky.attribute;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static me.randomhashtags.randompackage.util.listener.GivedpItem.givedpitem;

public class RemoveItem extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull Event event, HashMap<String, Entity> entities, HashMap<Entity, String> recipientValues, HashMap<String, String> valueReplacements) {
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof Player) {
                final String value = replaceValue(entities, recipientValues.get(e), valueReplacements);
                final ItemStack g = givedpitem.valueOf(value);
                if(g != null) {
                    removeItem((Player) e, g, g.getAmount());
                }
            }
        }
    }
}
