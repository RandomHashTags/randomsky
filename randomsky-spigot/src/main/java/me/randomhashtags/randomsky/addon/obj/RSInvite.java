package me.randomhashtags.randomsky.addon.obj;

import me.randomhashtags.randomsky.RSPlayer;
import me.randomhashtags.randomsky.RandomSky;
import me.randomhashtags.randomsky.addon.InviteType;
import me.randomhashtags.randomsky.event.InviteExpireEvent;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class RSInvite {
    public static final List<RSInvite> invites = new ArrayList<>();
    public final long createdTime;
    public final RSPlayer sender;
    public final UUID receiver;
    public final InviteType type;
    public final int expireTask;
    public RSInvite(long createdTime, RSPlayer sender, UUID receiver, InviteType type, int expiresInSeconds) {
        this.createdTime = createdTime;
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
        invites.add(this);
        expireTask = Bukkit.getScheduler().scheduleSyncDelayedTask(RandomSky.getPlugin, () -> {
            final InviteExpireEvent e = new InviteExpireEvent(this);
            Bukkit.getPluginManager().callEvent(e);
            delete();
        }, 20 * expiresInSeconds);
    }
    public void delete() {
        invites.remove(this);
    }
}
