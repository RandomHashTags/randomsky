package me.randomhashtags.randomsky.util;

import com.sun.istack.internal.Nullable;
import me.randomhashtags.randomsky.addon.*;
import me.randomhashtags.randomsky.addon.active.Home;
import me.randomhashtags.randomsky.addon.adventure.Adventure;
import me.randomhashtags.randomsky.addon.obj.AuctionedItemObj;
import me.randomhashtags.randomsky.addon.obj.ChatChannelsObj;
import me.randomhashtags.randomsky.addon.obj.CoinFlipStats;
import me.randomhashtags.randomsky.addon.obj.JackpotStats;
import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.universal.UMaterial;
import me.randomhashtags.randomsky.universal.UVersionable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static java.io.File.separator;

public class RSPlayer implements UVersionable, me.randomhashtags.randomsky.RSPlayer {
    private static final String folder = DATA_FOLDER + separator + "_Data" + separator + "players";
    public static final HashMap<UUID, RSPlayer> players = new HashMap<>();

    private boolean isLoaded = false, filter = false;
    private UUID uuid, allianceUUID, islandUUID;
    private File file;
    private YamlConfiguration yml;
    private ChatChannels chat;
    private PlayerRank rank;
    private CoinFlipStats coinflipStats;
    private JackpotStats jackpotStats;

    public long canDeleteIslandTime;
    private int skillTokens = 0;
    private ColorCrystal activeColorCrystal;

    private List<Home> homes;
    public List<AuctionedItemObj> auctions;
    private Set<Adventure> allowedAdventures;
    private Set<UMaterial> filteredItems;
    private Set<ColorCrystal> colorCrystals;

    private HashMap<CustomKit, Long> kitExpirations;
    private HashMap<PlayerSkill, Integer> playerSkills;
    private HashMap<ToggleType, Boolean> toggles;

    public RSPlayer(UUID uuid) {
        this.uuid = uuid;
        final File f = new File(folder, uuid.toString() + ".yml");
        boolean backup = false;
        if(!players.containsKey(uuid)) {
            if(!f.exists()) {
                try {
                    final File folder = new File(RSPlayer.folder);
                    if(!folder.exists()) {
                        folder.mkdirs();
                    }
                    f.createNewFile();
                    chat = new ChatChannelsObj();
                    backup = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            file = new File(folder, uuid.toString() + ".yml");
            yml = YamlConfiguration.loadConfiguration(file);
            players.put(uuid, this);
        }
        if(backup) backup();
    }
    public static RSPlayer get(UUID player) { return players.getOrDefault(player, new RSPlayer(player)).load(); }

    public boolean isOnline() {
        return Bukkit.getOfflinePlayer(uuid).isOnline();
    }
    public void backup() {
        yml.set("name", Bukkit.getOfflinePlayer(uuid).getName());
        if(rank != null) {
            yml.set("rank", rank.getIdentifier());
        }
        yml.set("island", islandUUID != null ? islandUUID.toString() : "null");
        yml.set("alliance", allianceUUID != null ? allianceUUID.toString() : "null");

        if(activeColorCrystal != null) {
            yml.set("color crystals.active", activeColorCrystal.getIdentifier());
        }
        if(colorCrystals != null) {
            yml.set("color crystals.owned", colorCrystals.toArray());
        }

        if(chat != null) {
            yml.set("chat.current", chat.getCurrent().name());
            yml.set("chat.active", chat.getActive().toArray());
        }

        if(kitExpirations != null) {
            yml.set("kits", null);
            for(CustomKit k : getKitExpirations().keySet()) {
                yml.set("kits." + k.getIdentifier() + ".expiration", kitExpirations.get(k));
            }
        }

        if(homes != null) {
            for(Home h : homes) {
                yml.set("homes." + h.getName(), h.getLocation().toString());
            }
        }

        if(allowedAdventures != null) {
            final List<String> adv = new ArrayList<>();
            for(Adventure a : getAllowedAdventures()) {
                adv.add(a.getIdentifier());
            }
            yml.set("adventures.allowed", adv);
        }

        yml.set("filter.enabled", filter);
        if(filteredItems != null) {
            final Set<String> filtered = new HashSet<>();
            for(UMaterial u : filteredItems) {
                filtered.add(u.name());
            }
            yml.set("filter.items", filtered);
        }

        yml.set("player skills.tokens", skillTokens);
        if(playerSkills != null) {
            for(PlayerSkill s : playerSkills.keySet()) {
                yml.set("player skills." + s.getIdentifier(), playerSkills.get(s));
            }
        }

        if(toggles != null) {
            for(ToggleType t : toggles.keySet()) {
                yml.set("toggles." + t.name(), toggles.get(t));
            }
        }

        save();
    }
    public RSPlayer load() {
        if(!isLoaded) {
            isLoaded = true;

            final String[] ints = yml.getString("ints").split(";"), longs = yml.getString("longs").split(";");
            final String island = yml.getString("island"), alliance = yml.getString("alliance");
            if(island != null && !island.equals("null")) {
                islandUUID = UUID.fromString(island);
            }
            if(alliance != null && !alliance.equals("null")) {
                allianceUUID = UUID.fromString(alliance);
            }
            filter = yml.getBoolean("filter.enabled");

            skillTokens = Integer.parseInt(ints[0]);
        }
        return this;
    }
    public void unload() {
        if(isLoaded) {
            isLoaded = false;
            players.remove(uuid);
        }
    }

    public boolean isLoaded() { return isLoaded; }
    public UUID getUUID() { return uuid; }
    public UUID getAllianceUUID() { return allianceUUID; }
    public void setAllianceUUID(UUID uuid) { allianceUUID = uuid; }
    public UUID getIslandUUID() { return islandUUID; }
    public void setIslandUUID(UUID uuid) { islandUUID = uuid; }
    public YamlConfiguration getYaml() { return yml; }

    public PlayerRank getRank() {
        if(rank == null) {
            final String R = yml.getString("rank", null);
            if(R != null && !R.equals("null")) {
                final Identifiable i = RSStorage.get(Feature.PLAYER_RANK, R);
                rank = i != null ? (PlayerRank) i : null;
            }
        }
        return rank;
    }
    public void setRank(@Nullable PlayerRank rank) {
        this.rank = rank;
    }
    public ChatChannels getChatChannels() {
        if(chat == null) {
            final ChatChannel current = ChatChannel.valueOf(yml.getString("chat.current"));
            final Set<ChatChannel> active = new HashSet<>();
            for(String s : yml.getStringList("chat.active")) {
                active.add(ChatChannel.valueOf(s));
            }
            chat = new ChatChannelsObj(current, active);
        }
        return chat;
    }
    public ColorCrystal getActiveColorCrystal() {
        if(activeColorCrystal == null) {
            final Identifiable i = RSStorage.get(Feature.COLOR_CRYSTAL, yml.getString("color crystals.active"));
            if(i != null) {
                activeColorCrystal = (ColorCrystal) i;
            }
        }
        return activeColorCrystal;
    }
    public Set<ColorCrystal> getColorCrystals() {
        if(colorCrystals == null) {
            colorCrystals = new HashSet<>();
            for(String s : yml.getStringList("color crystals.owned")) {
                final Identifiable i = RSStorage.get(Feature.COLOR_CRYSTAL, s);
                if(i != null) {
                    colorCrystals.add((ColorCrystal) i);
                }
            }
        }
        return colorCrystals;
    }
    public CoinFlipStats getCoinFlipStats() {
        if(coinflipStats == null) {
            final String pre = "stats.coinflip.";
            final BigDecimal wins = BigDecimal.valueOf(yml.getDouble(pre + "wins")), losses = BigDecimal.valueOf(yml.getDouble(pre + "losses")), wonCash = BigDecimal.valueOf(yml.getDouble(pre + "won cash"));
            final BigDecimal lostCash = BigDecimal.valueOf(yml.getDouble(pre + "lost cash")), taxesPaid = BigDecimal.valueOf(yml.getDouble(pre + "taxes paid"));
            coinflipStats = new CoinFlipStats(yml.getBoolean(pre + "notifications"), wins, losses, wonCash, lostCash, taxesPaid);
        }
        return coinflipStats;
    }
    public JackpotStats getJackpotStats() {
        if(jackpotStats == null) {
            final String pre = "stats.jackpot.";
            final BigDecimal ticketsPurchased = BigDecimal.valueOf(yml.getDouble(pre + "tickets purchased")), wonCash = BigDecimal.valueOf(yml.getDouble(pre + "won cash")), timesWon = BigDecimal.valueOf(yml.getDouble(pre + "times won"));
            jackpotStats = new JackpotStats(yml.getBoolean(pre + "notifications"), ticketsPurchased, wonCash, timesWon);
        }
        return jackpotStats;
    }

    public int getSkillTokens() { return skillTokens; }
    public void setSkillTokens(int skillTokens) { this.skillTokens = skillTokens; }

    public HashMap<CustomKit, Long> getKitExpirations() {
        if(kitExpirations == null) {
            kitExpirations = new HashMap<>();
            final ConfigurationSection kits = yml.getConfigurationSection("kits");
            if(kits != null) {
                for(String s : kits.getKeys(false)) {
                    final Identifiable i = RSStorage.get(Feature.CUSTOM_KIT, s);
                    if(i != null) {
                        kitExpirations.put((CustomKit) i, yml.getLong("kits." + s + ".expiration"));
                    }
                }
            }
        }
        return kitExpirations;
    }

    public List<Home> getHomes() {
        if(homes == null) {
            homes = new ArrayList<>();
            final ConfigurationSection h = yml.getConfigurationSection("homes");
            if(h != null) {
                for(String name : h.getKeys(false)) {
                    homes.add(new Home(name, yml.getLocation("homes." + name)));
                }
            }
        }
        return homes;
    }

    public Set<Adventure> getAllowedAdventures() {
        if(allowedAdventures == null) {
            allowedAdventures = new HashSet<>();
            for(String s : yml.getStringList("adventures.allowed")) {
                final Identifiable i = RSStorage.get(Feature.ADVENTURE, s);
                if(i != null) {
                    allowedAdventures.add((Adventure) i);
                }
            }
        }
        return allowedAdventures;
    }

    public boolean hasActiveFilter() { return filter; }
    public void setFilter(boolean active) { filter = active; }
    public Set<UMaterial> getFilteredItems() {
        if(filteredItems == null) {
            filteredItems = new HashSet<>();
            for(String s : yml.getStringList("filter.items")) {
                filteredItems.add(UMaterial.valueOf(s));
            }
        }
        return filteredItems;
    }

    public HashMap<PlayerSkill, Integer> getPlayerSkills() {
        if(playerSkills == null) {
            playerSkills = new HashMap<>();
            final ConfigurationSection c = yml.getConfigurationSection("player skills");
            if(c != null) {
                for(String s : c.getKeys(false)) {
                    if(!s.equals("tokens")) {
                        final Identifiable i = RSStorage.get(Feature.PLAYER_SKILL, s);
                        if(i != null) {
                            playerSkills.put((PlayerSkill) i, yml.getInt("player skills." + s));
                        }
                    }
                }
            }
        }
        return playerSkills;
    }

    public HashMap<ToggleType, Boolean> getToggles() {
        if(toggles == null) {
            toggles = new HashMap<>();
            for(String s : yml.getConfigurationSection("toggles").getKeys(false)) {
                toggles.put(ToggleType.valueOf(s), yml.getBoolean("toggles." + s));
            }
        }
        return toggles;
    }

    private void save() {
        try {
            yml.save(file);
            yml = YamlConfiguration.loadConfiguration(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
