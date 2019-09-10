package me.randomhashtags.randomsky.util;

import me.randomhashtags.randomsky.addon.FilterCategory;
import me.randomhashtags.randomsky.addon.RepairScroll;
import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.util.universal.UVersion;

import java.util.Map;
import java.util.TreeMap;

public abstract class RSStorage extends UVersion {
    protected static TreeMap<String, FilterCategory> filtercategories;
    protected static TreeMap<String, RepairScroll> repairscrolls;

    private void register(Identifiable object, String identity, Map tree) throws Exception {
        final String i = object.getIdentifier();
        if(tree.containsKey(i) || tree.containsValue(object)) {
            throw new Exception(identity + " with identifier \"" + i + "\" is already registered!");
        } else {
            tree.put(i, object);
        }
    }
    private void reg(Identifiable i, String identity, Map tree) {
        if(tree == null) tree = new TreeMap<>();
        try {
            register(i, identity, tree);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public FilterCategory getFilterCategory(String identifier) { return filtercategories != null ? filtercategories.get(identifier) : null; }
    public void addFilterCategory(FilterCategory category) { reg(category, "Filter Category", filtercategories); }

    public RepairScroll getRepairScroll(String identifier) { return repairscrolls != null ? repairscrolls.get(identifier) : null; }
    public void addRepairScroll(RepairScroll scroll) { reg(scroll, "Repair Scroll", repairscrolls); }
}
