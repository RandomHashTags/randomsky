package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.Itemable;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;

public interface ShopItem extends Itemable {
    BigDecimal getBuyPrice();
    BigDecimal getSellPrice();
    ItemStack getPurchasedItem();
    ItemStack getSoldItem();
}
