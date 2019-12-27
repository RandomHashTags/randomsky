package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.addon.util.Slotable;
import me.randomhashtags.randomsky.universal.UInventory;

import java.util.HashMap;

public interface ShopCategory extends Slotable, Itemable {
    UInventory getInventory();
    HashMap<Integer, ShopItem> getItems();
}
