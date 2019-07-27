package me.randomhashtags.randomsky.addons;

import java.util.UUID;

public abstract class Alliance {
    public abstract long getCreation();
    public abstract UUID getUUID();
    public abstract UUID getOwner();
    public abstract String getTag();
}
