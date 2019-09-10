package me.randomhashtags.randomsky.addon.realm;

import me.randomhashtags.randomsky.addon.adventure.Adventure;
import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.addon.util.RequiredIslandLevel;

import java.util.List;

public interface RealmPortal extends Itemable, RequiredIslandLevel {
    Adventure getFoundIn();
    List<Realm> getGivesAccessTo();
}
