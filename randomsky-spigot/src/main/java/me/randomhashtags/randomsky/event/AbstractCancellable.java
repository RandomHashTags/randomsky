package me.randomhashtags.randomsky.event;

import org.bukkit.event.Cancellable;

public abstract class AbstractCancellable extends AbstractEvent implements Cancellable {
    private boolean cancelled;
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}
