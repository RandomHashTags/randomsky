package me.randomhashtags.randomsky.event;

import java.math.BigDecimal;
import java.util.UUID;

public class CoinFlipEndEvent extends AbstractEvent {
    private UUID winner, loser;
    private BigDecimal wager, tax;
    public CoinFlipEndEvent(UUID winner, UUID loser, BigDecimal wager, BigDecimal tax) {
        this.winner = winner;
        this.loser = loser;
        this.wager = wager;
        this.tax = tax;
    }
    public UUID getWinner() { return winner; }
    public UUID getLoser() { return loser; }
    public BigDecimal getWager() { return wager; }
    public void setWager(BigDecimal wager) { this.wager = wager; }
    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }
}
