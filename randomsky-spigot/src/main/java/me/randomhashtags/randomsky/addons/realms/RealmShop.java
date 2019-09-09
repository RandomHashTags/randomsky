package me.randomhashtags.randomsky.addons.realms;

import me.randomhashtags.randomsky.addons.util.Inventoryable;

import java.util.HashMap;
import java.util.List;

public interface RealmShop extends Inventoryable {
    HashMap<Integer, List<String>> getCosts();
}
