package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.DoesCraft;
import org.bukkit.inventory.ItemStack;

public interface Scrap extends DoesCraft {
    boolean isSmeltable();
    int getChanceOfSmeltingIntoResult();
    ItemStack getSmeltedResult();
}
