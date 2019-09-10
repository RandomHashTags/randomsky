package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.Applyable;
import me.randomhashtags.randomsky.addon.util.Percentable;
import org.bukkit.inventory.ItemStack;

public interface RepairScroll extends Percentable, Applyable {
    int getPercentSlot();
    int getPercent(ItemStack is);
}
