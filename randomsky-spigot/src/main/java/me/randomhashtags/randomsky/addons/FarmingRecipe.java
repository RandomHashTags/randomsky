package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;
import me.randomhashtags.randomsky.utils.universal.UMaterial;

public interface FarmingRecipe extends Itemable {
    String getRecipeName();
    UMaterial getUnlocks();
}
