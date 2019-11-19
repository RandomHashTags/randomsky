package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.addon.util.Nameable;
import me.randomhashtags.randomsky.addon.util.Slotable;

import java.util.List;

public interface ItemRecipe extends Itemable, Slotable, Nameable {
    List<String> getIslandRequirements();
}
