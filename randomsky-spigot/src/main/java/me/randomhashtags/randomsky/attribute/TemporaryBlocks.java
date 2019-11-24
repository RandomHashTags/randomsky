package me.randomhashtags.randomsky.attribute;

import me.randomhashtags.randomsky.util.universal.UMaterial;
import org.bukkit.Location;

import java.util.HashMap;

public interface TemporaryBlocks {
    HashMap<Location, UMaterial> tempblocks = new HashMap<>();
}
