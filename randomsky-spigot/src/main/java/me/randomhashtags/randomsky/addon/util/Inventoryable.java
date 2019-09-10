package me.randomhashtags.randomsky.addon.util;

import me.randomhashtags.randomsky.util.universal.UInventory;

public interface Inventoryable extends Identifiable {
    String getTitle();
    UInventory getInventory();
}
