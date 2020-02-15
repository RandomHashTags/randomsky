package me.randomhashtags.randomsky.data;

import me.randomhashtags.randomsky.addon.CustomKit;

import java.util.HashMap;

public interface KitData {
    HashMap<CustomKit, Long> getExpirations();
}
