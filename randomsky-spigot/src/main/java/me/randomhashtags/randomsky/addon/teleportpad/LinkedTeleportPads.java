package me.randomhashtags.randomsky.addon.teleportpad;

import org.bukkit.Location;

import java.math.BigDecimal;
import java.util.HashMap;

public interface LinkedTeleportPads {
    TeleportPadSettings getSettings();
    Location getFrom();
    Location getTo();
    HashMap<String, BigDecimal> getEntitiesTransfered();
}
