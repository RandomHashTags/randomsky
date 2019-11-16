package me.randomhashtags.randomsky.event.island;

import me.randomhashtags.randomsky.addon.active.ActiveIslandChallenge;
import me.randomhashtags.randomsky.addon.island.Island;
import me.randomhashtags.randomsky.event.IslandEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.math.BigDecimal;

public class IslandChallengeProgressEvent extends IslandEvent {
    private ActiveIslandChallenge challenge;
    private BigDecimal increment;
    public IslandChallengeProgressEvent(Event event, Player player, Island island, ActiveIslandChallenge challenge, BigDecimal increment) {
        super(event, player, island);
        this.challenge = challenge;
        this.increment = increment;
    }
    public ActiveIslandChallenge getChallenge() { return challenge; }
    public BigDecimal getIncrement() { return increment; }
    public void setIncrement(BigDecimal increment) { this.increment = increment; }
}
