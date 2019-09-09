package me.randomhashtags.randomsky.addons.util;

import org.bukkit.inventory.ItemStack;

public interface Percentable extends Itemable {
    ItemStack getItem(int percent);
}
