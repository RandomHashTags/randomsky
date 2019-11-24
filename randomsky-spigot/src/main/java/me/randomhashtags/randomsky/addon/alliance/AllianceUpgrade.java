package me.randomhashtags.randomsky.addon.alliance;

import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.addon.util.MaxLevelable;
import me.randomhashtags.randomsky.addon.util.Slotable;

import java.util.HashMap;
import java.util.List;

public interface AllianceUpgrade extends Itemable, MaxLevelable, Slotable {
    HashMap<Integer, List<String>> getCost();
    HashMap<Integer, List<String>> getAttributes();
}
