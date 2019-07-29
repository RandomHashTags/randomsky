package me.randomhashtags.randomsky.addons;

import java.util.UUID;

public interface Alliance {
    long getCreation();
    UUID getUUID();
    UUID getOwner();
    String getTag();
}
