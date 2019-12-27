package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.addon.util.Nameable;
import me.randomhashtags.randomsky.universal.UMaterial;

import java.util.ArrayList;
import java.util.List;

public interface FarmingRecipe extends Itemable, Nameable {
    List<FarmingRecipe> defaults = new ArrayList<>();
    UMaterial getUnlocks();
}
