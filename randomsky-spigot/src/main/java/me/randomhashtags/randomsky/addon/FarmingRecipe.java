package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.util.universal.UMaterial;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public interface FarmingRecipe extends Itemable {
    List<FarmingRecipe> defaults = new ArrayList<>();
    UMaterial getUnlocks();
    String getRecipeName();
    BigDecimal getDailyLimit();
    BigDecimal getCompletion();
    String getType();
}
