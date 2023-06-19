package me.randomhashtags.randomsky.attribute;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class AddPotionEffect extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull Event event, HashMap<String, Entity> entities, HashMap<Entity, String> recipientValues, HashMap<String, String> valueReplacements) {
        for(Entity e : recipientValues.keySet()) {
            addPotionEffect(entities, e, recipientValues.get(e), valueReplacements);
        }
    }
    private void addPotionEffect(HashMap<String, Entity> entities, Entity entity, String value, HashMap<String, String> valueReplacements) {
        if(entity instanceof LivingEntity) {
            final LivingEntity l = (LivingEntity) entity;
            final String rv = replaceValue(entities, value, valueReplacements);
            if(rv != null) {
                final String[] values = rv.split(":");
                final PotionEffectType type = getPotionEffectType(values[0]);
                if(type != null) {
                    final int amplifier = (int) evaluate(values[1]), duration = (int) evaluate(values[2]);
                    l.addPotionEffect(new PotionEffect(type, duration, amplifier));
                    if(values.length >= 4 && Boolean.parseBoolean(values[3])) {
                        final HashMap<String, String> replacements = new HashMap<>();
                        if(valueReplacements != null) {
                            replacements.putAll(valueReplacements);
                        }
                        replacements.put("{POTION_EFFECT}", type.getName() + " " + api.toRoman(amplifier+1));
                        replacements.put("{DURATION_TICKS}", Integer.toString(duration));
                        replacements.put("{DURATION_SECONDS}", Integer.toString(duration/20));
                        replacements.put("{AMPLIFIER}", Integer.toString(amplifier));
                        replacements.put("{LEVEL}", Integer.toString(amplifier-1));
                        sendStringListMessage(entity, getRPConfig("custom enchants", "_settings.yml").getStringList("messages.apply potion effect"), replacements);
                    }
                }
            }
        }
    }
}
