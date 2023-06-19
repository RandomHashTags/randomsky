package me.randomhashtags.randomsky.attribute;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class RemovePotionEffect extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull Event event, HashMap<String, Entity> entities, HashMap<Entity, String> recipientValues, HashMap<String, String> valueReplacements) {
        for(Entity e : recipientValues.keySet()) {
            removePotionEffect(entities, e, recipientValues.get(e), valueReplacements);
        }
    }
    private void removePotionEffect(HashMap<String, Entity> entities, Entity entity, String value, HashMap<String, String> valueReplacements) {
        if(entity instanceof LivingEntity) {
            final LivingEntity l = (LivingEntity) entity;
            final String[] values = replaceValue(entities, value, valueReplacements).split(":");
            final PotionEffectType type = getPotionEffectType(values[0]);
            if(type != null) {
                l.removePotionEffect(type);
                if(values.length >= 2 && Boolean.parseBoolean(values[1])) {
                    final HashMap<String, String> replacements = new HashMap<>();
                    if(valueReplacements != null) {
                        replacements.putAll(valueReplacements);
                    }
                    replacements.put("{POTION_EFFECT}", type.getName());
                    sendStringListMessage(entity, getRPConfig("custom enchants", "_settings.yml").getStringList("messages.remove potion effect"), replacements);
                }
            }
        }
    }
}
