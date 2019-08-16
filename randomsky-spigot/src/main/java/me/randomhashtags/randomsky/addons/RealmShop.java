package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Inventoryable;

import java.util.HashMap;
import java.util.List;

public interface RealmShop extends Inventoryable {
    HashMap<Integer, List<String>> getCosts();
}
