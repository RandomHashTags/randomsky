package me.randomhashtags.randomsky.attribute;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Freeze extends AbstractEventAttribute {
    private static HashMap<Player, TObject> tasks;
    @Override
    public void load() {
        super.load();
        tasks = new HashMap<>();
    }
    @Override
    public void unload() {
        for(Player p : tasks.keySet()) {
            final TObject t = tasks.get(p);
            SCHEDULER.cancelTask((int) t.getFirst());
            p.setWalkSpeed((float) t.getSecond());
        }
        tasks = null;
    }
    @Override
    public void execute(@NotNull Event event, String value) {
        final PlayerQuitEvent q = event instanceof PlayerQuitEvent ? (PlayerQuitEvent) event : null;
        if(q != null) {
            final Player player = q.getPlayer();
            final TObject t = tasks.getOrDefault(player, null);
            if(t != null) {
                SCHEDULER.cancelTask((int) t.getFirst());
                player.setWalkSpeed((float) t.getSecond());
                tasks.remove(player);
            }
        }
    }
    @Override
    public void execute(@NotNull Event event, HashMap<String, Entity> entities, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final Player player = e instanceof Player ? (Player) e : null;
            if(player != null) {
                final float ws = player.getWalkSpeed();
                player.setWalkSpeed(0);
                final TObject t = new TObject(SCHEDULER.scheduleSyncDelayedTask(RANDOM_SKY, () -> {
                    player.setWalkSpeed(ws);
                    tasks.remove(player);
                }, (int) evaluate(recipientValues.get(e))), ws, null);
                tasks.put(player, t);
            }
        }
    }
}
