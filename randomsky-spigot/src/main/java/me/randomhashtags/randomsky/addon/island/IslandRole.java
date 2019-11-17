package me.randomhashtags.randomsky.addon.island;

import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.addon.util.Nameable;

import java.util.List;

public interface IslandRole extends Identifiable, Nameable {
    String getRank();
    List<String> getLore();
    List<String> getPermissions();
}
