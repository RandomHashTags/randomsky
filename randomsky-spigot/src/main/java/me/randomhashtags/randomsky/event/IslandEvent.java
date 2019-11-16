package me.randomhashtags.randomsky.event;

import me.randomhashtags.randomsky.addon.island.Island;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class IslandEvent extends RSEventCancellable {
    private Event event;
    private Island island;
    public IslandEvent(Event event, Player player, Island island) {
        super(player);
        this.event = event;
        this.island = island;
    }
    public Event getEvent() { return event; }
    public Island getIsland() { return island; }
}
