package me.randomhashtags.randomsky.util;

import me.randomhashtags.randomsky.addon.*;
import me.randomhashtags.randomsky.addon.adventure.Adventure;
import me.randomhashtags.randomsky.addon.bot.AutoBot;
import me.randomhashtags.randomsky.addon.island.IslandSkill;
import me.randomhashtags.randomsky.addon.realm.RealmEvent;
import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.util.universal.UVersion;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class RSStorage extends UVersion {
    protected static LinkedHashMap<String, Adventure> adventures;
    protected static LinkedHashMap<String, AutoBot> autobots;
    protected static LinkedHashMap<String, PlayerSkill> playerskills;
    protected static LinkedHashMap<String, FarmingRecipe> farmingrecipes;
    protected static LinkedHashMap<String, FilterCategory> filtercategories;
    protected static LinkedHashMap<String, IslandSkill> islandskills;
    protected static LinkedHashMap<String, Outpost> outposts;
    protected static LinkedHashMap<String, PermissionBlock> permissionblocks;
    protected static LinkedHashMap<String, RealmEvent> realmevents;
    protected static LinkedHashMap<String, RepairScroll> repairscrolls;
    protected static LinkedHashMap<String, ResourceNode> resourcenodes;
    protected static LinkedHashMap<String, ShopCategory> shopcategories;

    private void register(Identifiable object, String identity, Map tree) throws Exception {
        final String i = object.getIdentifier();
        if(tree.containsKey(i) || tree.containsValue(object)) {
            throw new Exception(identity + " with identifier \"" + i + "\" is already registered!");
        } else {
            tree.put(i, object);
        }
    }
    private void reg(Identifiable i, String identity, Map tree) {
        if(tree == null) tree = new LinkedHashMap<>();
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
