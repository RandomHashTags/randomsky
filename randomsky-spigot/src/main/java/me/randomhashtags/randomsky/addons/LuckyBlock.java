package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.objects.KitItem;
import me.randomhashtags.randomsky.addons.utils.Itemable;

import java.util.LinkedHashMap;
import java.util.List;

public interface LuckyBlock extends Itemable {
    LinkedHashMap<Integer, CustomKit> getChangeToUnlockKits();
    List<KitItem> getOtherGear();
}
