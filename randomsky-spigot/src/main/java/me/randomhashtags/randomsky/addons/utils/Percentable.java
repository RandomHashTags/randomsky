package me.randomhashtags.randomsky.addons.utils;

import org.bukkit.inventory.ItemStack;

public interface Percentable extends Itemable {
    ItemStack getItem(int percent);
}
