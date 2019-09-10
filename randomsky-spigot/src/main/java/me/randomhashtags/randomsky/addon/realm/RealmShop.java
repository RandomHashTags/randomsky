package me.randomhashtags.randomsky.addon.realm;

import me.randomhashtags.randomsky.addon.util.Inventoryable;

import java.util.HashMap;
import java.util.List;

public interface RealmShop extends Inventoryable {
    HashMap<Integer, List<String>> getCosts();
}
