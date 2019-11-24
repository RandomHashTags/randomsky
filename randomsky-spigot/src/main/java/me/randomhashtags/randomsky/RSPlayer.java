package me.randomhashtags.randomsky;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import me.randomhashtags.randomsky.addon.*;
import me.randomhashtags.randomsky.addon.active.Home;
import me.randomhashtags.randomsky.addon.adventure.Adventure;
import me.randomhashtags.randomsky.addon.alliance.Alliance;
import me.randomhashtags.randomsky.addon.island.Island;
import me.randomhashtags.randomsky.addon.obj.CoinFlipStats;
import me.randomhashtags.randomsky.addon.obj.JackpotStats;
import me.randomhashtags.randomsky.util.ToggleType;
import me.randomhashtags.randomsky.util.universal.UMaterial;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface RSPlayer {
    HashMap<UUID, RSPlayer> CACHE = new HashMap<>();

    static RSPlayer get(@NotNull UUID player) {
        return CACHE.getOrDefault(player, null);
    }

    RSPlayer load();
    void unload();

    boolean isLoaded();
    UUID getUUID();
    UUID getAllianceUUID();
    void setAllianceUUID(UUID uuid);
    UUID getIslandUUID();
    void setIslandUUID(UUID uuid);

    default Alliance getAlliance() {
        return Alliance.CACHE.getOrDefault(getAllianceUUID(), null);
    }
    default Island getIsland() {
        return Island.CACHE.getOrDefault(getIslandUUID(), null);
    }

    PlayerRank getRank();
    void setRank(@Nullable PlayerRank rank);
    ChatChannels getChatChannels();
    CoinFlipStats getCoinFlipStats();
    JackpotStats getJackpotStats();

    int getSkillTokens();
    void setSkillTokens(int skillTokens);

    List<Home> getHomes();
    default boolean deleteHome(@NotNull String name) {
        final List<Home> homes = getHomes();
        if(homes != null) {
            for(Home h : homes) {
                if(name.equals(h.name)) {
                    homes.remove(h);
                    return true;
                }
            }
        }
        return false;
    }
    default boolean setHome(@NotNull String name, @NotNull Location l) {
        final List<Home> homes = getHomes();
        if(homes != null) {
            for(Home h : homes) {
                if(name.equals(h.name)) {
                    h.location = l;
                    return true;
                }
            }
            homes.add(new Home(name, l));
            return true;
        }
        return false;
    }
    Set<Adventure> getAllowedAdventures();

    boolean hasActiveFilter();
    void setFilter(boolean active);
    Set<UMaterial> getFilteredItems();

    HashMap<CustomKit, Long> getKitExpirations();
    default void setKitExpiration(CustomKit kit, long expiration) {
        final HashMap<CustomKit, Long> e = getKitExpirations();
        if(e != null) {
            e.put(kit, expiration);
        }
    }

    HashMap<PlayerSkill, Integer> getPlayerSkills();
    default PlayerSkillLevel getPlayerSkillLevel(@NotNull PlayerSkill skill) {
        final HashMap<PlayerSkill, Integer> a = getPlayerSkills();
        final Integer target = a != null ? a.get(skill) : null;
        if(target != null) {
            final HashMap<Integer, PlayerSkillLevel> levels = skill.getLevels();
            return levels != null && levels.containsKey(target) ? levels.get(target) : null;
        }
        return null;
    }

    HashMap<ToggleType, Boolean> getToggles();
    default boolean isToggleEnabled(@NotNull ToggleType type) {
        final HashMap<ToggleType, Boolean> values = getToggles();
        return values != null && values.containsKey(type) ? values.get(type) : type.getDefaultValue();
    }
    default void setToggle(@NotNull ToggleType type, boolean value) {
        final HashMap<ToggleType, Boolean> values = getToggles();
        if(values != null) {
            values.put(type, value);
        }
    }
    default void toggleToggle(@NotNull ToggleType type) {
        final HashMap<ToggleType, Boolean> values = getToggles();
        if(values != null) {
            values.put(type, !(values.containsKey(type) ? values.get(type) : type.getDefaultValue()));
        }
    }
}
