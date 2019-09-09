package me.randomhashtags.randomsky.addons.util;

import java.util.List;

public interface Rewardable {
    int getMinRewardSize();
    int getMaxRewardSize();
    List<String> getRewards();
}
