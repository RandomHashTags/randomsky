package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Applyable;
import me.randomhashtags.randomsky.addons.utils.Percentable;
import org.bukkit.inventory.ItemStack;

public interface RepairScroll extends Percentable, Applyable {
    int getPercentSlot();
    int getPercent(ItemStack is);
}
