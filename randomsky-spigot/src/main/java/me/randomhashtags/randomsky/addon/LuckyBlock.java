package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.obj.KitItem;
import me.randomhashtags.randomsky.addon.util.Itemable;

import java.util.LinkedHashMap;
import java.util.List;

public interface LuckyBlock extends Itemable {
    LinkedHashMap<Integer, CustomKit> getChangeToUnlockKits();
    List<KitItem> getOtherGear();
}
