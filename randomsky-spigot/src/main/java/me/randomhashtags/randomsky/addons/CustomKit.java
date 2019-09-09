package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.obj.KitItem;
import me.randomhashtags.randomsky.addons.util.Itemable;

import java.util.List;

public interface CustomKit extends Itemable {
    int getSlot();
    long getCooldown();
    List<KitItem> getItems();
    String getPermission();
}
