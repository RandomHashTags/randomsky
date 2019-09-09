package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.util.Itemable;
import org.bukkit.inventory.Recipe;

import java.util.List;

public interface CustomRecipeItem extends Itemable {
    List<Recipe> getRecipes();
}
