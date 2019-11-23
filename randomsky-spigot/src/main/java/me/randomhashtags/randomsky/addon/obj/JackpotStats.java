package me.randomhashtags.randomsky.addon.obj;

import java.math.BigDecimal;

public class JackpotStats {
    private boolean notifications;
    private BigDecimal ticketsPurchased, wonCash, timesWon;
    public JackpotStats(boolean notifications, BigDecimal ticketsPurchased, BigDecimal wonCash, BigDecimal timesWon) {
        this.notifications = notifications;
        this.ticketsPurchased = ticketsPurchased;
        this.wonCash = wonCash;
        this.timesWon = timesWon;
    }
    public boolean receivesNotifications() { return notifications; }
    public void setReceivesNotifications(boolean receives) { notifications = receives; }
    public BigDecimal getTicketsPurchased() { return ticketsPurchased; }
    public void setTicketsPurchased(BigDecimal ticketsPurchased) { this.ticketsPurchased = ticketsPurchased; }
    public BigDecimal getWonCash() { return wonCash; }
    public void setWonCash(BigDecimal wonCash) { this.wonCash = wonCash; }
    public BigDecimal getTimesWon() { return timesWon; }
    public void setTimesWon(BigDecimal timesWon) { this.timesWon = timesWon; }
}
