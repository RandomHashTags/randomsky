package me.randomhashtags.randomsky.addons.alliance;

public interface AllianceRelationship {
    long getRequestedTime();
    AllianceRelation getRelation();
    boolean isPending();
}
