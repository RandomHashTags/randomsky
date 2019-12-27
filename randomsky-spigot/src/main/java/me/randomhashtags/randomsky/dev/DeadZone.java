package me.randomhashtags.randomsky.dev;

import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.universal.UMaterial;

import java.util.List;

public interface DeadZone extends Identifiable {
    List<UMaterial> getOnlyPlaceableMaterials();
    long getResetInterval();
}
