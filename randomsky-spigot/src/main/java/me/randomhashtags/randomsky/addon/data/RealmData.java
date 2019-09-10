package me.randomhashtags.randomsky.addon.data;

import me.randomhashtags.randomsky.addon.realm.Realm;
import me.randomhashtags.randomsky.util.universal.UInventory;

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
