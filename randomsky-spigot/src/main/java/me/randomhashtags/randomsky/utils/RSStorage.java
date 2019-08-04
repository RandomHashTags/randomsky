package me.randomhashtags.randomsky.utils;

import me.randomhashtags.randomsky.addons.FilterCategory;
import me.randomhashtags.randomsky.addons.RepairScroll;
import me.randomhashtags.randomsky.addons.utils.Identifiable;
import me.randomhashtags.randomsky.utils.universal.UVersion;

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
    public void registerFilterCategory(FilterCategory category) { reg(category, "Filter Category", filtercategories); }

    public RepairScroll getRepairScroll(String identifier) { return repairscrolls != null ? repairscrolls.get(identifier) : null; }
    public void registerRepairScroll(RepairScroll scroll) { reg(scroll, "Repair Scroll", repairscrolls); }
}
