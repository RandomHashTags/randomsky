package me.randomhashtags.randomsky.event;

import me.randomhashtags.randomsky.addon.obj.RSInvite;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class InviteExpireEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public final RSInvite invite;
    public InviteExpireEvent(RSInvite invite) {
        this.invite = invite;
    }
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
