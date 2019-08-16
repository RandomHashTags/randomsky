package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.RequiredIslandLevel;

import java.util.HashMap;
import java.util.List;

public interface Realm extends RequiredIslandLevel {
    HashMap<String, List<String>> getMessages();
    String getRealmName();
    RealmObjective getRealmObjective();
    RealmPortal getRealmRequiredPortal();
    RealmEvent getRealmEvent();
    RealmShop getRealmShop();
    long getRealmMaxTimeAllowedPerDay();
}
