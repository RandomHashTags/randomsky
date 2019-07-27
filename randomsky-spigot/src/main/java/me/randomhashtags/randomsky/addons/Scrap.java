package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.DoesCraft;
import org.bukkit.inventory.ItemStack;

public abstract class Scrap extends DoesCraft {
    public abstract boolean isSmeltable();
    public abstract int getChanceOfSmeltingIntoResult();
    public abstract ItemStack getSmeltedResult();
}
