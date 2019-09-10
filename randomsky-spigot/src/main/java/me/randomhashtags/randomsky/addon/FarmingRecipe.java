package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.util.universal.UMaterial;

public interface FarmingRecipe extends Itemable {
    String getRecipeName();
    UMaterial getUnlocks();
}
