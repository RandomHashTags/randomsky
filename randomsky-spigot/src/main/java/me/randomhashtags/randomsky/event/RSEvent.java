package me.randomhashtags.randomsky.event;

import org.bukkit.entity.Player;

public abstract class RSEvent extends AbstractEvent {
    private Player player;
    public RSEvent(Player player) {
        this.player = player;
    }
    public Player getPlayer() { return player; }
}
