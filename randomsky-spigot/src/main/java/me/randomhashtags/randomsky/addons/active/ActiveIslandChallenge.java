package me.randomhashtags.randomsky.addons.active;

import me.randomhashtags.randomsky.addons.island.IslandChallenge;

import java.math.BigDecimal;

public class ActiveIslandChallenge {
    private IslandChallenge challenge;
    private BigDecimal progress;
    private boolean claimedRewards;
    public ActiveIslandChallenge(IslandChallenge challenge, BigDecimal progress, boolean claimedRewards) {
        this.challenge = challenge;
        this.progress = progress;
        this.claimedRewards = claimedRewards;
    }
    public boolean isCompleted() { return progress.doubleValue() >= challenge.getCompletion().doubleValue(); }
    public boolean didClaimRewards() { return claimedRewards; }
    public IslandChallenge getChallenge() { return challenge; }
    public BigDecimal getProgress() { return progress; }
    public void setProgress(BigDecimal progress) { this.progress = progress; }
}
