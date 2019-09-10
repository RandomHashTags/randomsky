package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.obj.KitItem;
import me.randomhashtags.randomsky.addon.util.Itemable;

import java.util.List;

public interface CustomKit extends Itemable {
    int getSlot();
    long getCooldown();
    List<KitItem> getItems();
    String getPermission();
}
