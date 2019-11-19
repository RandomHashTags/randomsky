package me.randomhashtags.randomsky.addon.realm;

import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.addon.util.Nameable;
import me.randomhashtags.randomsky.addon.util.RequiredIslandLevel;
import me.randomhashtags.randomsky.addon.util.Slotable;

import java.util.HashMap;
import java.util.List;

public interface Realm extends Itemable, RequiredIslandLevel, Slotable, Nameable {
    HashMap<String, List<String>> getMessages();
    RealmObjective getObjective();
    RealmPortal getPortal();
    RealmEvent getEvent();
    RealmShop getShop();
    long getMaxTimeAllowedPerDay();
}
