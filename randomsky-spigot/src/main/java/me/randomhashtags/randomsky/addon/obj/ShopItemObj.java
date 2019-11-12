package me.randomhashtags.randomsky.addon.obj;

import me.randomhashtags.randomsky.addon.ShopItem;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;

public class ShopItemObj implements ShopItem {
    private String key;
    private int slot;
    private BigDecimal buyPrice, sellPrice;

    public ShopItemObj(String key, int slot, BigDecimal buyPrice, BigDecimal sellPrice) {
        this.key = key;
        this.slot = slot;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public String getIdentifier() { return key; }
    public int getSlot() { return slot; }
    public BigDecimal getBuyPrice() { return buyPrice; }
    public BigDecimal getSellPrice() { return sellPrice; }

    public ItemStack getItem() { return null; }
    public ItemStack getPurchasedItem() { return null; }
    public ItemStack getSoldItem() { return null; }
}
