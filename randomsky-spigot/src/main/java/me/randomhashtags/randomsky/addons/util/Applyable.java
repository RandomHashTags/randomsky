package me.randomhashtags.randomsky.addons.util;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Applyable {
    List<String> getAppliesTo();
    boolean canBeApplied(ItemStack itemstack);
}
