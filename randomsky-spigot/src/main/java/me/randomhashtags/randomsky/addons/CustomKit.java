package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.objects.KitItem;
import me.randomhashtags.randomsky.addons.utils.Itemable;

import java.util.List;

public abstract class CustomKit extends Itemable {
    public abstract int getSlot();
    public abstract long getCooldown();
    public abstract List<KitItem> getItems();
    public abstract String getPermission();
}
