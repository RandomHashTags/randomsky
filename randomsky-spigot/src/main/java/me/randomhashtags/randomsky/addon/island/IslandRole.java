package me.randomhashtags.randomsky.addon.island;

import me.randomhashtags.randomsky.addon.util.Identifiable;

import java.util.List;

public interface IslandRole extends Identifiable {
    String getRank();
    String getName();
    List<String> getLore();
    List<String> getPermissions();
}
