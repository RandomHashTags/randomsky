package me.randomhashtags.randomsky.dev;

import me.randomhashtags.randomsky.util.RSFeature;

public class DeadZones extends RSFeature {
    private static DeadZones instance;
    public static DeadZones getDeadZones() {
        if(instance == null) instance = new DeadZones();
        return instance;
    }


    public void load() {
    }
    public void unload() {
    }
}
