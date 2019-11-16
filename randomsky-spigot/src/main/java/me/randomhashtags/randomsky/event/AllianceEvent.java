package me.randomhashtags.randomsky.event;

import me.randomhashtags.randomsky.addon.alliance.Alliance;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class AllianceEvent extends RSEventCancellable {
    private Event event;
    private Alliance alliance;
    public AllianceEvent(Event event, Player player, Alliance alliance) {
        super(player);
        this.event = event;
        this.alliance = alliance;
    }
    public Event getEvent() { return event; }
    public Alliance getAlliance() { return alliance; }
}
