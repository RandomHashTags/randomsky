package me.randomhashtags.randomsky.data;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface AllianceData {
    UUID getAlliance();
    void setAlliance(@NotNull UUID alliance);
}
