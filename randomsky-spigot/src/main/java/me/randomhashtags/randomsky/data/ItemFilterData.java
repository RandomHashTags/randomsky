package me.randomhashtags.randomsky.data;

import me.randomhashtags.randomsky.universal.UMaterial;

import java.util.List;

public interface ItemFilterData {
    boolean isEnabled();
    List<UMaterial> getFilteredItems();
}
