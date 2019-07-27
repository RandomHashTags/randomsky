package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;
import me.randomhashtags.randomsky.utils.universal.UMaterial;

public abstract class FarmingRecipe extends Itemable {
    public abstract String getRecipeName();
    public abstract UMaterial getUnlocks();
}
