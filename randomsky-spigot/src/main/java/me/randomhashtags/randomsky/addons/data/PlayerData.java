package me.randomhashtags.randomsky.addons.data;

import me.randomhashtags.randomsky.addons.*;
import me.randomhashtags.randomsky.addons.active.Home;
import me.randomhashtags.randomsky.addons.adventure.Adventure;
import me.randomhashtags.randomsky.addons.alliance.Alliance;
import me.randomhashtags.randomsky.addons.island.Island;
import me.randomhashtags.randomsky.addons.util.Loadable;
import me.randomhashtags.randomsky.utils.universal.UMaterial;
import org.bukkit.Location;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PlayerData extends Loadable {
    long getJoinedTime();
    UUID getUUID();
    Team getTeam();
    Island getIsland();
    Alliance getAlliance();

    PlayerRank getRank();

    CoinFlipData getCoinFlipData();
    RealmData getRealmData();

    List<Adventure> getAllowedAdventures();
    List<Home> getHomes();
    void setHomes(List<Home> homes);
    default boolean setHome(String identifier, Location l) {
        final List<Home> homes = getHomes();
        if(homes != null && !homes.isEmpty()) {
            for(Home h : homes) {
                if(h.name.equalsIgnoreCase(identifier)) {
                    return true;
                }
            }
        }
        return false;
    }

    List<UMaterial> getFilteredItems();
    void setFilteredItems(List<UMaterial> filtered);


    BigDecimal getSkillTokens();
    void setSkillTokens(BigDecimal d);
    default void addSkillTokens(BigDecimal tokens) { setSkillTokens(tokens); }
    default void setSkillTokens(int tokens) { setSkillTokens(BigDecimal.valueOf(tokens)); }
    default void setSkillTokens(double tokens) { setSkillTokens(BigDecimal.valueOf(tokens)); }
}
