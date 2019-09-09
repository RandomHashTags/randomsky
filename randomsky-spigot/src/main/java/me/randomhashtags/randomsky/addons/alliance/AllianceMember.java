package me.randomhashtags.randomsky.addons.alliance;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public interface AllianceMember {
    long getJoined();
    UUID getUUID();
    AllianceRole getRole();
    default OfflinePlayer getPlayer() { return Bukkit.getOfflinePlayer(getUUID()); }
}
