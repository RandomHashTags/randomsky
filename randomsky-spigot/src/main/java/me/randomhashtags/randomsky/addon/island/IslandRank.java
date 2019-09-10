package me.randomhashtags.randomsky.addon.island;

import me.randomhashtags.randomsky.addon.util.Identifiable;

import java.util.List;

public interface IslandRank extends Identifiable {
    String getString();
    List<String> getAddedPermissions();
}
