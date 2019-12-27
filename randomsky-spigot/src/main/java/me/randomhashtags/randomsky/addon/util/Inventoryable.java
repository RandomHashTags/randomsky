package me.randomhashtags.randomsky.addon.util;

import me.randomhashtags.randomsky.universal.UInventory;

public interface Inventoryable extends Identifiable {
    String getTitle();
    UInventory getInventory();
}
