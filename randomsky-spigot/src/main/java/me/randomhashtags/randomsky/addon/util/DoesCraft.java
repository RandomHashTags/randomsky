package me.randomhashtags.randomsky.addon.util;

import org.bukkit.inventory.Recipe;

import java.util.List;

public interface DoesCraft extends Itemable {
    List<Recipe> getCrafts();
}
