package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.Itemable;

import java.util.List;

public interface CollectionChest extends Itemable {
    List<String> getPicksUp();
}
