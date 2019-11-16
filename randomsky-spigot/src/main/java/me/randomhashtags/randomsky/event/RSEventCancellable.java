package me.randomhashtags.randomsky.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public abstract class RSEventCancellable extends RSEvent implements Cancellable {
    private boolean cancelled;
    public RSEventCancellable(Player player) {
        super(player);
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}
