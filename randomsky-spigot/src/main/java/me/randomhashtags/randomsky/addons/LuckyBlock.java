package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.obj.KitItem;
import me.randomhashtags.randomsky.addons.util.Itemable;

import java.util.LinkedHashMap;
import java.util.List;

public interface LuckyBlock extends Itemable {
    LinkedHashMap<Integer, CustomKit> getChangeToUnlockKits();
    List<KitItem> getOtherGear();
}
