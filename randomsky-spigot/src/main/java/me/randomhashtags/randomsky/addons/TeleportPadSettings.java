package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.utils.universal.UInventory;
import me.randomhashtags.randomsky.utils.universal.UMaterial;

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
