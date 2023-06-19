package me.randomhashtags.randomsky.data;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface IslandData {
    UUID getOwnedIsland();
    void setOwnedIsland(@NotNull UUID island);
}
