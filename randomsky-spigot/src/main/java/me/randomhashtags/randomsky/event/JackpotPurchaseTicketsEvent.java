package me.randomhashtags.randomsky.event;

import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class JackpotPurchaseTicketsEvent extends RSEventCancellable {
    private BigDecimal amount, price;
    public JackpotPurchaseTicketsEvent(Player player, BigDecimal amount, BigDecimal price) {
        super(player);
        this.amount = amount;
        this.price = price;
    }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
