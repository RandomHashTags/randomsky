package me.randomhashtags.randomsky.addon.file;

import me.randomhashtags.randomsky.addon.alliance.Alliance;
import me.randomhashtags.randomsky.addon.alliance.AllianceUpgrade;
import me.randomhashtags.randomsky.util.RSAddon;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class FileAlliance extends RSAddon implements Alliance {
    private String[] info, ints;

    private long creation;
    private UUID uuid, owner;
    private String tag;
    private boolean isLoaded;
    private List<AllianceUpgrade> upgrades;
    private int maxMemberSize = -999;
    private List<UUID> members;

    public FileAlliance(File f) {
        load(f);
    }

    public UUID getUUID() {
        if(uuid == null) uuid = UUID.fromString(getYamlName());
        return uuid;
    }

    public boolean isLoaded() { return isLoaded; }

    private String[] getInfo() {
        if(info == null) info = yml.getString("info").split(";");
        return info;
    }
    private String[] getInts() {
        if(ints == null) ints = yml.getString("ints").split(";");
        return ints;
    }

    public long getCreation() {
        if(creation == 0) creation = Long.parseLong(getInfo()[0]);
        return creation;
    }
    public UUID getOwner() {
        if(owner == null) owner = UUID.fromString(getInfo()[1]);
        return owner;
    }
    public String getTag() {
        if(tag == null) tag = getInfo()[2];
        return tag;
    }

    public List<AllianceUpgrade> getUpgrades() {
        if(upgrades == null) {
            upgrades = new ArrayList<>();
        }
        return upgrades;
    }

    public int getMaxMemberSize() {
        if(maxMemberSize == -999) maxMemberSize = Integer.parseInt(getInts()[0]);
        return maxMemberSize;
    }
    public void setMaxMemberSize(int size) { maxMemberSize = size; }

    public List<UUID> getMembers() {
        if(members == null) {
            members = new ArrayList<>();
        }
        return members;
    }
}
