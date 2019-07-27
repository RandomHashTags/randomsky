package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.objects.KitItem;
import me.randomhashtags.randomsky.addons.utils.Itemable;

import java.util.LinkedHashMap;
import java.util.List;

public abstract class LuckyBlock extends Itemable {
    public abstract LinkedHashMap<Integer, CustomKit> getChangeToUnlockKits();
    public abstract List<KitItem> getOtherGear();
}
