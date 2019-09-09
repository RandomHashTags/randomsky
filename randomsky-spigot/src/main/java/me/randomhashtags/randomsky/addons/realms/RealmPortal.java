package me.randomhashtags.randomsky.addons.realms;

import me.randomhashtags.randomsky.addons.adventure.Adventure;
import me.randomhashtags.randomsky.addons.util.Itemable;
import me.randomhashtags.randomsky.addons.util.RequiredIslandLevel;

import java.util.List;

public interface RealmPortal extends Itemable, RequiredIslandLevel {
    Adventure getFoundIn();
    List<Realm> getGivesAccessTo();
}
