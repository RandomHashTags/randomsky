package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.DoesCraft;
import org.bukkit.inventory.ItemStack;

public interface Scrap extends DoesCraft {
    public abstract boolean isSmeltable();
    public abstract int getChanceOfSmeltingIntoResult();
    public abstract ItemStack getSmeltedResult();
}
