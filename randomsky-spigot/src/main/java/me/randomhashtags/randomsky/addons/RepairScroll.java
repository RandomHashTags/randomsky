package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class RepairScroll extends Itemable {
    public abstract int getMinPercent();
    public abstract int getMaxPercent();
    public abstract List<String> getAppliesTo();
    public boolean canBeApplied(ItemStack is) {
        if(is != null) {
            final String m = is.getType().name();
            for(String s : getAppliesTo()) {
                if(m.endsWith(s.toUpperCase())) {
                    return true;
                }
            }
        }
        return false;
    }
}
