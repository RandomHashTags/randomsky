package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Identifiable;
import me.randomhashtags.randomsky.utils.universal.UMaterial;

import java.util.List;

public interface DeadZone extends Identifiable {
    List<UMaterial> getOnlyPlaceableMaterials();
    long getResetInterval();
}
