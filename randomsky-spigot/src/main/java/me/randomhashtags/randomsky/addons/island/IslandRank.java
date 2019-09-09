package me.randomhashtags.randomsky.addons.island;

import me.randomhashtags.randomsky.addons.util.Identifiable;

import java.util.List;

public interface IslandRank extends Identifiable {
    String getString();
    List<String> getAddedPermissions();
}
