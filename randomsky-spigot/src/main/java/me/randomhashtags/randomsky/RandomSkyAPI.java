package me.randomhashtags.randomsky;

import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RandomSkyFeature;
import org.jetbrains.annotations.NotNull;

public enum RandomSkyAPI implements RSFeature {
    INSTANCE;

    @Override
    public @NotNull RandomSkyFeature get_feature() {
        return RandomSkyFeature.API;
    }

    public void load() {
    }
    public void unload() {
    }
}
