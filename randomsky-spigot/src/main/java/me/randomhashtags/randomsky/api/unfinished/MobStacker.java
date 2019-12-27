package me.randomhashtags.randomsky.api.unfinished;

import me.randomhashtags.randomsky.util.RSFeature;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.List;

public class MobStacker extends RSFeature implements Listener {
    private static MobStacker instance;
    public static MobStacker getMobStacker() {
        if(instance == null) instance = new MobStacker();
        return instance;
    }

    public YamlConfiguration config;
    private List<World> worlds;
    private HashMap<World, Integer> tasks, checkIntervals, radius;

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "mob stacker.yml");
        worlds = Bukkit.getWorlds();
        tasks = new HashMap<>();
        checkIntervals = new HashMap<>();
        radius = new HashMap<>();
        sendConsoleMessage("&6[RandomSky] &aLoaded Mob Stacker &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        for(World w : tasks.keySet()) {
            SCHEDULER.cancelTask(tasks.get(w));
        }
        tasks = null;
        StackedEntity.stacked.clear();
    }

    public void stack() {
    }
    public void stack(World world) {
    }
    public void stack(List<EntityType> types) {
        for(World w : worlds) {

        }
    }
    public void stack(World world, List<EntityType> types) {
        for(LivingEntity e : world.getLivingEntities()) {
        }
    }

    @EventHandler
    private void creatureSpawnEvent(CreatureSpawnEvent event) {
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        final Entity e = event.getEntity();
        if(!event.isCancelled() && e instanceof LivingEntity && !(e instanceof Player)) {
            final LivingEntity le = (LivingEntity) e;
        }
    }
    @EventHandler(priority = EventPriority.LOW)
    private void entityDamageEvent(EntityDamageEvent event) {
    }
}
