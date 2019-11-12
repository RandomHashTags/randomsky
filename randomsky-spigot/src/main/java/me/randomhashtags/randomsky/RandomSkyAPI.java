package me.randomhashtags.randomsky;

import me.randomhashtags.randomsky.util.RSFeature;

public class RandomSkyAPI extends RSFeature {
    private static RandomSkyAPI instance;
    public static RandomSkyAPI getAPI() {
        if(instance == null) instance = new RandomSkyAPI();
        return instance;
    }

    public void load() {
    }
    public void unload() {
    }
}
