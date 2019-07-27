package me.randomhashtags.randomsky.utils;

import me.randomhashtags.randomsky.addons.FilterCategory;
import me.randomhashtags.randomsky.addons.Island;
import me.randomhashtags.randomsky.addons.ResourceNode;
import me.randomhashtags.randomsky.utils.universal.UVersion;

import java.util.LinkedHashMap;
import java.util.UUID;

public abstract class RSStorage extends UVersion {
    protected static LinkedHashMap<String, FilterCategory> filtercategories;

    public FilterCategory getFilterCategory(String identifier) {
        return filtercategories != null ? filtercategories.get(identifier) : null;
    }
    public void addFilterCategory(FilterCategory category) {
        if(filtercategories == null) filtercategories = new LinkedHashMap<>();
        filtercategories.put(category.getIdentifier(), category);
    }
}
