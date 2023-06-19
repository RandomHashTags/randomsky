package me.randomhashtags.randomsky.supported.mechanics;

import me.randomhashtags.randomsky.RandomSky;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public enum SpawnerAPI {
    INSTANCE;

    private String plugin;
    private Object util;

    public void reload() {
        final String plugin = RandomSky.spawner;
        instance.plugin = plugin;
        if(plugin != null && plugin.equals("SilkSpawners")) {
            instance.util = de.dustplanet.util.SilkUtil.hookIntoSilkSpanwers();
        }
    }

    public ItemStack getItem(@NotNull String entitytype) {
        if(plugin != null) {
            switch (plugin) {
                case "EpicSpawners5": return get_item_epic_spawners_5(entitytype);
                case "EpicSpawners6": return get_item_epic_spawners_6(entitytype);
                case "SilkSpawners":  return get_item_silk_spawners(entitytype);
                default: return null;
            }
        }
        return null;
    }

    private ItemStack get_item_epic_spawners_5(@NotNull String entitytype) {
        final String type = entitytype.toUpperCase().replace("_", "").replace(" ", "");
        com.songoda.epicspawners.api.spawner.SpawnerData data = null;
        for(com.songoda.epicspawners.api.spawner.SpawnerData spawnerData : com.songoda.epicspawners.EpicSpawnersPlugin.getInstance().getSpawnerManager().getAllSpawnerData()) {
            final String compare = spawnerData.getIdentifyingName().toUpperCase().replace("_", "").replace(" ", "");
            if(type.equals(compare)) data = spawnerData;
        }
        return data != null ? data.toItemStack() : null;
    }
    private ItemStack get_item_epic_spawners_6(@NotNull String entitytype) {
        final String type = entitytype.toUpperCase().replace("_", "").replace(" ", "");
        com.songoda.epicspawners.spawners.spawner.SpawnerData data = null;
        for(com.songoda.epicspawners.spawners.spawner.SpawnerData spawnerData : com.songoda.epicspawners.EpicSpawners.getInstance().getSpawnerManager().getAllSpawnerData()) {
            final String compare = spawnerData.getIdentifyingName().toUpperCase().replace("_", "").replace(" ", "");
            if(type.equals(compare)) data = spawnerData;
        }
        return data != null ? data.toItemStack() : null;
    }
    private ItemStack get_item_silk_spawners(@NotNull String entitytype) {
        final String input = entitytype.toUpperCase().replace("_", "").replace(" ", "");
        for(EntityType t : EntityType.values()) {
            if(input.equals(t.name().replace("_", "").replace(" ", ""))) {
                final short id = t.getTypeId();
                return ((de.dustplanet.util.SilkUtil) util).newSpawnerItem(id, ((de.dustplanet.util.SilkUtil) util).getCustomSpawnerName(((de.dustplanet.util.SilkUtil) util).getCreatureName(id)), 1, false);
            }
        }
        return null;
    }
}
