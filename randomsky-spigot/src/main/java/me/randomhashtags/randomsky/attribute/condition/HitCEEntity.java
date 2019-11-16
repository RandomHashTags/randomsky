package me.randomhashtags.randomsky.attribute.condition;

import me.randomhashtags.randomsky.attribute.AbstractEventCondition;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class HitCEEntity extends AbstractEventCondition {
    @Override
    public boolean check(Event event, Entity entity) {
        if(event instanceof EntityDamageByEntityEvent) {
            final EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            return e.getDamager().equals(entity) && LivingCustomEnchantEntity.living.containsKey(e.getEntity().getUniqueId());
        }
        return false;
    }
}
