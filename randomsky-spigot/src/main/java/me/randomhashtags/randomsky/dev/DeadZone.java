package me.randomhashtags.randomsky.dev;

import me.randomhashtags.randomsky.addons.util.Identifiable;
import me.randomhashtags.randomsky.utils.universal.UMaterial;

import java.util.List;

public interface DeadZone extends Identifiable {
    List<UMaterial> getOnlyPlaceableMaterials();
    long getResetInterval();
}
