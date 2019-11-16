package me.randomhashtags.randomsky.addon.util;

import me.randomhashtags.randomsky.addon.PlayerRank;

public interface RequiredPlayerRank extends Identifiable {
    PlayerRank getMinimumPlayerRankToUse();
}
