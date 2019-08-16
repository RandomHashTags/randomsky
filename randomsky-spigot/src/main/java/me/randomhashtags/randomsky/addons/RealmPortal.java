package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;
import me.randomhashtags.randomsky.addons.utils.RequiredIslandLevel;

import java.util.List;

public interface RealmPortal extends Itemable, RequiredIslandLevel {
    Adventure getFoundIn();
    List<Realm> getGivesAccessTo();
}
