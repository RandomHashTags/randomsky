package me.randomhashtags.randomsky.addon.file;

import me.randomhashtags.randomsky.addon.InventoryPet;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSAddon;
import me.randomhashtags.randomsky.util.RSStorage;

import java.io.File;

public class FileInventoryPet extends RSAddon implements InventoryPet {
    public FileInventoryPet(File f) {
        load(f);
        RSStorage.register(Feature.INVENTORY_PET, this);
    }
}
