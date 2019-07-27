package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;
import org.bukkit.inventory.Recipe;

import java.util.List;

public abstract class CustomRecipeItem extends Itemable {
    public abstract List<Recipe> getRecipes();
}
