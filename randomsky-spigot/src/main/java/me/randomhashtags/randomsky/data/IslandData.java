package me.randomhashtags.randomsky.data;

import java.util.UUID;

public interface IslandData {
    UUID getOwnedIsland();
    void setOwnedIsland(@NotNull UUID island);
}
