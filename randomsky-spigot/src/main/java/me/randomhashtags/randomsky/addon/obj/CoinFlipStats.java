package me.randomhashtags.randomsky.addon.obj;

import java.math.BigDecimal;

public class CoinFlipStats {
    private boolean notifications;
    private BigDecimal wins, losses, wonCash, lostCash, taxesPaid;
    public CoinFlipStats(boolean notifications, BigDecimal wins, BigDecimal losses, BigDecimal wonCash, BigDecimal lostCash, BigDecimal taxesPaid) {
        this.notifications = notifications;
        this.wins = wins;
        this.losses = losses;
        this.wonCash = wonCash;
        this.lostCash = lostCash;
        this.taxesPaid = taxesPaid;
    }
    public boolean receivesNotifications() { return notifications; }
    public void setReceivesNotifications(boolean receives) { notifications = receives; }
    public BigDecimal getWins() { return wins; }
    public void setWins(BigDecimal wins) { this.wins = wins; }
    public BigDecimal getLosses() { return losses; }
    public void setLosses(BigDecimal losses) { this.losses = losses; }
    public BigDecimal getWonCash() { return wonCash; }
    public void setWonCash(BigDecimal wonCash) { this.wonCash = wonCash; }
    public BigDecimal getLostCash() { return lostCash; }
    public void setLostCash(BigDecimal lostCash) { this.lostCash = lostCash; }
    public BigDecimal getTaxesPaid() { return taxesPaid; }
    public void setTaxesPaid(BigDecimal taxesPaid) { this.taxesPaid = taxesPaid; }
}
