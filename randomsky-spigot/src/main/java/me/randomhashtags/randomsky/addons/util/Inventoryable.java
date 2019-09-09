package me.randomhashtags.randomsky.addons.util;

import me.randomhashtags.randomsky.utils.universal.UInventory;

public interface Inventoryable extends Identifiable {
    String getTitle();
    UInventory getInventory();
}
