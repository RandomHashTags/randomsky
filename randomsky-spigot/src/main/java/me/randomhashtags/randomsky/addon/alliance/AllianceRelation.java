package me.randomhashtags.randomsky.addon.alliance;

import me.randomhashtags.randomsky.addon.util.Identifiable;

public interface AllianceRelation extends Identifiable {
    String getColor();
    boolean isDamageable();
}
