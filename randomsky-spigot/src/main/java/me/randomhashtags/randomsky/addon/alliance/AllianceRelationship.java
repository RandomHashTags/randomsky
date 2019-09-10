package me.randomhashtags.randomsky.addon.alliance;

public interface AllianceRelationship {
    long getRequestedTime();
    AllianceRelation getRelation();
    boolean isPending();
}
