package me.randomhashtags.randomsky.addon.alliance;

import me.randomhashtags.randomsky.addon.util.Loadable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface Alliance extends Loadable {
    HashMap<UUID, Alliance> cached = new HashMap<>();

    boolean isLoaded();
    long getCreation();
    UUID getUUID();
    UUID getOwner();
    String getTag();

    List<AllianceUpgrade> getUpgrades();

    int getMaxMemberSize();
    void setMaxMemberSize(int size);
    List<UUID> getMembers();
    default List<AllianceMember> getOnlineMembers() {
        final List<AllianceMember> a = new ArrayList<>();
        final List<UUID> m = getMembers();
        if(m != null && !m.isEmpty()) {
            for(UUID u : m) {
                final OfflinePlayer p = Bukkit.getPlayer(u);
                if(p != null && p.isOnline()) {
                    a.add(p.getPlayer());
                }
            }
        }
        return a;
    }

    HashMap<UUID, AllianceRelationship> getRelations();

    HashMap<String, Double> getMultipliers();
    void setMultipliers(HashMap<String, Double> multipliers);
    default double getMultiplier(String identifier) {
        final HashMap<String, Double> m = getMultipliers();
        return m != null ? m.get(identifier) : -1;
    }
    default void setMultiplier(String identifier, double multiplier) {
        final HashMap<String, Double> m = getMultipliers();
        if(m != null) {
            m.put(identifier, multiplier);
        }
    }

    void disband();
}
