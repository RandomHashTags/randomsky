package me.randomhashtags.randomsky.addons.data;

import me.randomhashtags.randomsky.addons.Realm;
import me.randomhashtags.randomsky.utils.universal.UInventory;

import java.util.HashMap;

public interface RealmData {
    UInventory getTeleportedFromWorldInventory();
    UInventory getRealmInventory();
    HashMap<Realm, Integer> getRealmPoints();
    default void addPoints(Realm realm, int points) {
        if(realm != null) {
            final HashMap<Realm, Integer> r = getRealmPoints();
            if(r != null && r.containsKey(realm)) {
                r.put(realm, r.get(realm)+points);
            }
        }
    }
}
