package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Identifiable;

import java.util.HashMap;
import java.util.List;

public interface AllianceUpgrade extends Identifiable {
    int getMaxLevel();
    HashMap<Integer, List<String>> getCost();
    HashMap<Integer, List<String>> getAttributes();
}
