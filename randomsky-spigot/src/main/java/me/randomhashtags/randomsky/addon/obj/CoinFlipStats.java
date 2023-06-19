package me.randomhashtags.randomsky.addon.obj;

import java.math.BigDecimal;

public class CoinFlipStats {
    public boolean receives_notifications;
    public BigDecimal wins, losses, cash_won, cash_lost, taxes_paid;
    public CoinFlipStats(boolean receives_notifications, BigDecimal wins, BigDecimal losses, BigDecimal cash_won, BigDecimal cash_lost, BigDecimal taxes_paid) {
        this.receives_notifications = receives_notifications;
        this.wins = wins;
        this.losses = losses;
        this.cash_won = cash_won;
        this.cash_lost = cash_lost;
        this.taxes_paid = taxes_paid;
    }
}
