package me.randomhashtags.randomsky.addon.teleportpad;

import me.randomhashtags.randomsky.util.universal.UInventory;
import me.randomhashtags.randomsky.util.universal.UMaterial;

import java.util.List;

public interface TeleportPadSettings {
    boolean teleportsPlayers();
    boolean teleportsItems();
    boolean playsParticles();
    boolean isWhitelistOnly();
    UInventory getSettingsGUI();
    UInventory getMaterialWhitelistGUI();
    List<UMaterial> getMaterialWhitelist();
}
