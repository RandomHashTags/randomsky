package me.randomhashtags.randomsky.addon.obj;

import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.UUID;

public class AuctionedItemObj {
    private long auctionTime;
    private UUID auctioner;
    private ItemStack item;
    private BigDecimal price;
    private boolean claimable;
    public AuctionedItemObj(long auctionTime, UUID auctioner, ItemStack item, BigDecimal price) {
        this.auctionTime = auctionTime;
        this.auctioner = auctioner;
        this.item = item;
        this.price = price;
    }
    public long getAuctionedTime() { return auctionTime; }
    public void setAuctionTime(long auctionTime) { this.auctionTime = auctionTime; }
    public UUID getAuctioner() { return auctioner; }
    public ItemStack getItem() { return item.clone(); }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public boolean isClaimable() { return claimable; }
    public void setClaimable(boolean claimable) { this.claimable = claimable; }
}
