package me.randomhashtags.randomsky.addons.active;

import me.randomhashtags.randomsky.addons.island.IslandUpgrade;

public class ActiveIslandUpgrade {
    private IslandUpgrade upgrade;
    private int tier;
    public ActiveIslandUpgrade(IslandUpgrade upgrade, int tier) {
        this.upgrade = upgrade;
        this.tier = tier;
    }
    public IslandUpgrade getUpgrade() { return upgrade; }
    public int getTier() { return tier; }
    public void setTier(int tier) { this.tier = tier; }
}
