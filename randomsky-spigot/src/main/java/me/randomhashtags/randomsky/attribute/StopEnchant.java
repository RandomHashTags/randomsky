package me.randomhashtags.randomsky.attribute;

import me.randomhashtags.randomsky.addon.enchant.CustomEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class StopEnchant extends AbstractEventAttribute {
    private static HashMap<UUID, HashMap<CustomEnchant, TObject>> stoppedEnchants;
    @Override
    public void load() {
        super.load();
        stoppedEnchants = new HashMap<>();
    }
    @Override
    public void unload() {
        for(UUID u : stoppedEnchants.keySet()) {
            stopTasks(u);
        }
        stoppedEnchants = null;
    }
    @Override
    public void execute(@NotNull Event event) {
        if(event instanceof CustomEnchantProcEvent) {
            final CustomEnchantProcEvent c = (CustomEnchantProcEvent) event;
            final UUID u = c.getEntities().get("Player").getUniqueId();
            if(stoppedEnchants.containsKey(u)) {
                final CustomEnchant e = c.getEnchant();
                final HashMap<CustomEnchant, TObject> t = stoppedEnchants.get(u);
                if(t.containsKey(e)) {
                    final TObject o = t.get(e);
                    if(c.getEnchantLevel() <= (int) o.getFirst()) {
                        c.setCancelled(true);
                    }
                }
            }
        }
    }
    @Override
    public void execute(@NotNull Event event, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final CustomEnchant enchant = valueOfCustomEnchant(recipientValues.get(e));
            if(enchant != null && enchant.isEnabled()) {
                final String[] v = recipientValues.get(e).split(":");
                final int level = (int) evaluate(v[0].replace("max", Integer.toString(enchant.getMaxLevel())));
                final UUID u = e.getUniqueId();
                if(!stoppedEnchants.containsKey(u)) stoppedEnchants.put(u, new HashMap<>());
                final HashMap<CustomEnchant, TObject> a = stoppedEnchants.get(u);
                final int task = resumeEnchant(u, enchant, (int) evaluate(v[1]));
                a.put(enchant, new TObject(level, task, null));
            }
        }
    }
    private void stopTasks(UUID uuid) {
        for(TObject t : stoppedEnchants.get(uuid).values()) {
            SCHEDULER.cancelTask((int) t.getSecond());
        }
    }
    private int resumeEnchant(UUID player, CustomEnchant enchant, int ticks) {
        return SCHEDULER.scheduleSyncDelayedTask(RANDOM_SKY, () -> {
            stoppedEnchants.get(player).remove(enchant);
        }, ticks);
    }
}
