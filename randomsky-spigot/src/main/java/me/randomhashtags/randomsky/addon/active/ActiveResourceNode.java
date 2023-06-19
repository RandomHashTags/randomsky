package me.randomhashtags.randomsky.addon.active;

import me.randomhashtags.randomsky.RandomSkyAPI;
import me.randomhashtags.randomsky.addon.ResourceNode;
import me.randomhashtags.randomsky.universal.UMaterial;
import me.randomhashtags.randomsky.universal.UVersionable;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class ActiveResourceNode implements UVersionable {
    private UUID uuid;
    private ResourceNode type;
    private Location location;
    private long cooldownExpiration;

    private boolean hasPlaced;
    private int respawnTask;

    public ActiveResourceNode(ResourceNode type, Location location) {
        this(UUID.randomUUID(), type, location, System.currentTimeMillis()+type.getRespawnTime()*1000, true);
    }
    public ActiveResourceNode(UUID uuid, ResourceNode type, Location location, long cooldownExpiration, boolean place) {
        this.uuid = uuid;
        this.type = type;
        this.location = location;
        this.cooldownExpiration = cooldownExpiration;
        island.activeResourceNodes.add(this);
        this.hasPlaced = place;
        doTask();
        if(place) {
            place();
        } else {
            island.unplacedResourceNodes.add(this);
        }
    }

    public UUID getUUID() { return uuid; }
    public ResourceNode getType() { return type; }
    public Location getLocation() { return location; }
    public World getWorld() { return location.getWorld(); }
    public long getCooldownExpiration() { return cooldownExpiration; }
    public int getRespawnTask() { return respawnTask; }


    public void place() {
        final Block b = getWorld().getBlockAt(location);
        final UMaterial h = type.getNodeBlock();
        final ItemStack i = h.getItemStack();
        b.setType(i.getType());
        final BlockState bs = b.getState();
        bs.setRawData(i.getData().getData());
        bs.update();
        hasPlaced = true;
    }
    private void doTask() {
        respawnTask = SCHEDULER.scheduleSyncDelayedTask(RANDOM_SKY, () -> {
            final World w = getWorld();
            final Block b = w.getBlockAt(location);
            final UMaterial h = type.getHarvestBlock();
            final ItemStack i = h.getItemStack();
            b.setType(i.getType());
            final BlockState bs = b.getState();
            bs.setRawData(i.getData().getData());
            bs.update();
            w.spawnParticle(Particle.BLOCK_CRACK, location.clone().add(0.5, 1, 0.5), 50, i.getData());
        }, 20*type.getRespawnTime());
    }
    public void delete() {
        SCHEDULER.cancelTask(respawnTask);
        getWorld().getBlockAt(location).setType(Material.AIR);
    }
    public void harvest(Player player) {
        final HarvestResourceNodeEvent e = new HarvestResourceNodeEvent(player, this);
        PLUGIN_MANAGER.callEvent(e);
        if(!e.isCancelled()) {
            final World world = getWorld();
            final HashMap<ResourceNode, Integer> mined = e.getIsland().minedResourceNodes;
            if(!mined.containsKey(type)) mined.put(type, 0);
            mined.put(type, mined.get(type)+1);
            final Location l = location.clone().add(0.5, 1, 0.5);
            for(String s : type.getLoot()) {
                final ItemStack is = RandomSkyAPI.INSTANCE.d(null, s.split(";")[0]);
                final Item item = world.dropItem(l, is);
                item.setPickupDelay(10);
                item.setVelocity(new Vector(0, 0, 0));
            }
            cooldownExpiration = System.currentTimeMillis()+type.getRespawnTime()*1000;
            final Block b = world.getBlockAt(location);
            final UMaterial n = type.getNodeBlock();
            b.setType(n.getMaterial());
            final BlockState bs = b.getState();
            bs.setRawData(n.getData());
            bs.update();
            doTask();
        }
    }
}
