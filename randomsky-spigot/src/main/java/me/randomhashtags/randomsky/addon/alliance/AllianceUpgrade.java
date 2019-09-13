package me.randomhashtags.randomsky.addon.alliance;

import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.addon.util.MaxLevelable;

import java.util.HashMap;
import java.util.List;

public interface AllianceUpgrade extends Identifiable, MaxLevelable {
    HashMap<Integer, List<String>> getCost();
    HashMap<Integer, List<String>> getAttributes();
}
