package me.randomhashtags.randomsky.addon.file;

import me.randomhashtags.randomsky.addon.alliance.AllianceRelation;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSAddon;
import me.randomhashtags.randomsky.util.RSStorage;

import java.io.File;

public class FileAllianceRelation extends RSAddon implements AllianceRelation {
    private String color;
    private boolean damageable;
    public FileAllianceRelation(File f) {
        load(f);
        RSStorage.register(Feature.ALLIANCE_RELATION, this);
        damageable = yml.getBoolean("settings.damageable");
    }

    public String getIdentifier() { return getYamlName(); }

    public String getColor() {
        if(color == null) {
            color = colorize(yml.getString("settings.color"));
        }
        return color;
    }
    public boolean isDamageable() {
        return damageable;
    }
}
