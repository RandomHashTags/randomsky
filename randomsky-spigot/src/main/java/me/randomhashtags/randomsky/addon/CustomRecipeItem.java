package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.Itemable;
import org.bukkit.inventory.Recipe;

import java.util.List;

public interface CustomRecipeItem extends Itemable {
    List<Recipe> getRecipes();
}
