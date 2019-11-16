package me.randomhashtags.randomsky.attributesys;

import me.randomhashtags.randomsky.addon.EventAttributeListener;
import me.randomhashtags.randomsky.event.*;
import me.randomhashtags.randomsky.event.armor.ArmorEquipEvent;
import me.randomhashtags.randomsky.event.armor.ArmorPieceBreakEvent;
import me.randomhashtags.randomsky.event.armor.ArmorSwapEvent;
import me.randomhashtags.randomsky.event.armor.ArmorUnequipEvent;
import me.randomhashtags.randomsky.event.async.ItemNameTagUseEvent;
import me.randomhashtags.randomsky.event.enchant.CustomEnchantApplyEvent;
import me.randomhashtags.randomsky.event.enchant.CustomEnchantProcEvent;
import me.randomhashtags.randomsky.event.enchant.PlayerRevealCustomEnchantEvent;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EACoreListener extends EventExecutor implements Listener {
    private static EACoreListener instance;
    public static EACoreListener getEventAttributeListener() {
        if(instance == null) instance = new EACoreListener();
        return instance;
    }

    private static List<EventAttributeListener> eventListeners;

    public String getIdentifier() { return "EVENT_ATTRIBUTE_CORE_LISTENER"; }

    public void load() {
        eventListeners = new ArrayList<>();
    }
    public void unload() {
        eventListeners = null;
    }

    public final void registerEventAttributeListener(EventAttributeListener listener) {
        if(eventListeners == null) {
            eventListeners = new ArrayList<>();
        }
        if(listener != null && !eventListeners.contains(listener)) {
            eventListeners.add(listener);
        }
    }
    public final void callEventAttributeListeners(Event event) {
        if(eventListeners != null) {
            for(EventAttributeListener f : eventListeners) {
                f.called(event);
            }
        }
    }
    public final void unregisterEventAttributeListener(EventAttributeListener listener) {
        if(listener != null && eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void creatureSpawnEvent(CreatureSpawnEvent event) {
        if(event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {
            final UUID u = event.getEntity().getUniqueId();
            if(!SPAWNED_FROM_SPAWNER.contains(u)) {
                SPAWNED_FROM_SPAWNER.add(u);
            }
        }
    }
    @EventHandler(priority = EventPriority.LOWEST)
    private void entityShootBowEvent(EntityShootBowEvent event) {
        PROJECTILE_EVENTS.put(event.getProjectile().getUniqueId(), event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void projectileHitEvent(ProjectileHitEvent event) {
        PROJECTILE_EVENTS.remove(event.getEntity().getUniqueId());
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityDamageEvent(EntityDamageEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityDeathEvent(EntityDeathEvent event) {
        callEventAttributeListeners(event);
        SPAWNED_FROM_SPAWNER.remove(event.getEntity().getUniqueId());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void blockPlaceEvent(BlockPlaceEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void blockBreakEvent(BlockBreakEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerFishEvent(PlayerFishEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerJoinEvent(PlayerJoinEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerQuitEvent(PlayerQuitEvent event) {
        callEventAttributeListeners(event);
    }
    /*
     * RandomSky Events
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerRevealCustomEnchantEvent(PlayerRevealCustomEnchantEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void coinFlipEndEvent(CoinFlipEndEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void fundDepositEvent(FundDepositEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void customEnchantProcEvent(CustomEnchantProcEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void customEnchantApplyEvent(CustomEnchantApplyEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void jackpotPurchaseTicketsEvent(JackpotPurchaseTicketsEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void shopPurchaseEvent(ShopPurchaseEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void shopSellEvent(ShopSellEvent event) {
        callEventAttributeListeners(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void armorEquipEvent(ArmorEquipEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void armorUnequipEvent(ArmorUnequipEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void armorPieceBreakEvent(ArmorPieceBreakEvent event) {
        callEventAttributeListeners(event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void armorSwapEvent(ArmorSwapEvent event) {
        callEventAttributeListeners(event);
    }
}
