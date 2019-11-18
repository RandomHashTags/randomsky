package me.randomhashtags.randomsky.util;

import me.randomhashtags.randomsky.addon.PlayerSkill;
import me.randomhashtags.randomsky.addon.adventure.Adventure;
import me.randomhashtags.randomsky.addon.alliance.Alliance;
import me.randomhashtags.randomsky.addon.PlayerRank;
import me.randomhashtags.randomsky.addon.active.Home;
import me.randomhashtags.randomsky.addon.island.Island;
import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.util.universal.UMaterial;
import me.randomhashtags.randomsky.util.universal.UVersionable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static java.io.File.separator;

public class RSPlayer implements UVersionable {
    private static final String folder = dataFolder + separator + "_Data" + separator + "players";
    public static final HashMap<UUID, RSPlayer> players = new HashMap<>();

    private boolean isLoaded = false;
    private UUID uuid;
    private File file;
    private YamlConfiguration yml;
    private Island island;
    private Alliance alliance;
    private UUID islandUUID, allianceUUID;
    private ChatChannels chat;
    private PlayerRank rank;

    public BigDecimal canDeleteIslandTime, coinflipWonCash, coinflipLostCash, coinflipTaxesPaid, jackpotWonCash;
    public int skillTokens = 0, coinflipWins = 0, coinflipLosses = 0, jackpotTickets = 0, jackpotWins = 0;
    public boolean tpaRequests = true, privateMessage = true, filterChat = false, islandInviteNotifications = true, payRequests = true, memberVisiting = true, punchToKick = true, instantBlockBreak = true, instantBreakPickup = true,
            clearInventoryConfirmation = true, auctionBuyConfirm = true, auctionSellConfirm = true, coinflipNotifications = true, bleedNotifications = true, enchantDebug = true, breakParticles = true,
            filter = false, instaBreakTutorial = true, jackpotCountdown = true;

    private List<Home> homes;
    public List<AuctionedItem> auctions;
    private List<Adventure> allowedAdventures;
    public List<ActivePlayerSkill> skills;
    private List<UMaterial> filteredItems;

    private HashMap<Kit, Long> kitExpirations;

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
                    chat = new ChatChannels(ChatChannel.GLOBAL, true, true, true, false, false, false);
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

        final ChatChannels cc = getChat();
        final String chat = cc.current + ";" + cc.global + ";" + cc.island + ";" + cc.alliance + ";" + cc.ally + ";" + cc.truce + ";" + cc.local;
        final String booleans = tpaRequests + ";" + privateMessage + ";" + filterChat + ";" + islandInviteNotifications + ";" + payRequests + ";" + memberVisiting + ";" + punchToKick + ";" + instantBlockBreak + ";" + instantBreakPickup + ";" + clearInventoryConfirmation + ";" + auctionBuyConfirm + ";" + auctionSellConfirm + ";" + coinflipNotifications + ";" + bleedNotifications + ";" + enchantDebug + ";" + breakParticles + ";" + filter + ";" + instaBreakTutorial + ";" + jackpotCountdown;
        final String ints = skillTokens + ";" + coinflipWins + ";" + coinflipLosses + ";" + jackpotTickets + ";" + jackpotWins;
        final String longs = coinflipWonCash + ";" + coinflipLostCash + ";" + coinflipTaxesPaid + ";" + jackpotWonCash;
        yml.set("booleans", booleans);
        yml.set("ints", ints);
        yml.set("longs", longs);
        yml.set("chat", chat);
        yml.set("kits", null);
        for(Kit k : getKitExpirations().keySet()) {
            yml.set("kits." + k.getYamlName(), kitExpirations.get(k));
        }
        if(homes != null) {
            for(Home h : homes) {
                yml.set("homes." + h.name, h.location.toString());
            }
        }
        if(filteredItems != null) {
            final List<String> filtered = new ArrayList<>();
            for(UMaterial u : filteredItems) {
                filtered.add(u.name());
            }
            yml.set("filtered items", filtered);
        }

        if(allowedAdventures != null) {
            final List<String> adv = new ArrayList<>();
            for(Adventure a : getAllowedAdventures()) {
                adv.add(a.getIdentifier());
            }
            yml.set("allowed adventures", adv);
        }

        save();
    }
    public RSPlayer load() {
        if(!isLoaded) {
            isLoaded = true;

            final String[] booleans = yml.getString("booleans").split(";"), ints = yml.getString("ints").split(";"), longs = yml.getString("longs").split(";");
            final String U = yml.getString("island"), UU = yml.getString("alliance");
            if(U != null && !U.equals("null")) {
                islandUUID = UUID.fromString(U);
                island = Island.get(islandUUID);
                island.load();
            }
            if(UU != null && !UU.equals("null")) {
                allianceUUID = UUID.fromString(UU);
                alliance = Alliance.get(allianceUUID);
                alliance.load();
            }
            tpaRequests = Boolean.parseBoolean(booleans[0]);
            privateMessage = Boolean.parseBoolean(booleans[1]);
            filterChat = Boolean.parseBoolean(booleans[2]);
            islandInviteNotifications = Boolean.parseBoolean(booleans[3]);
            payRequests = Boolean.parseBoolean(booleans[4]);
            memberVisiting = Boolean.parseBoolean(booleans[5]);
            punchToKick = Boolean.parseBoolean(booleans[6]);
            instantBlockBreak = Boolean.parseBoolean(booleans[7]);
            instantBreakPickup = Boolean.parseBoolean(booleans[8]);
            clearInventoryConfirmation = Boolean.parseBoolean(booleans[9]);
            auctionBuyConfirm = Boolean.parseBoolean(booleans[10]);
            auctionSellConfirm = Boolean.parseBoolean(booleans[11]);
            coinflipNotifications = Boolean.parseBoolean(booleans[12]);
            bleedNotifications = Boolean.parseBoolean(booleans[13]);
            enchantDebug = Boolean.parseBoolean(booleans[14]);
            breakParticles = Boolean.parseBoolean(booleans[15]);
            filter = Boolean.parseBoolean(booleans[16]);
            instaBreakTutorial = Boolean.parseBoolean(booleans[17]);
            jackpotCountdown = Boolean.parseBoolean(booleans[18]);

            skillTokens = Integer.parseInt(ints[0]);
            coinflipWins = Integer.parseInt(ints[1]);
            coinflipLosses = Integer.parseInt(ints[2]);
            jackpotTickets = Integer.parseInt(ints[3]);
            jackpotWins = Integer.parseInt(ints[4]);

            coinflipWonCash = Long.parseLong(longs[0]);
            coinflipLostCash = Long.parseLong(longs[1]);
            coinflipTaxesPaid = Long.parseLong(longs[2]);
            jackpotWonCash = Long.parseLong(longs[3]);
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
    public YamlConfiguration getYaml() { return yml; }
    public Island getIsland() { return island; }
    public void setIsland(Island island) {
        this.island = island;
        islandUUID = island != null ? island.getUUID() : null;
    }
    public Alliance getAlliance() { return alliance; }
    public void setAlliance(Alliance alliance) {
        this.alliance = alliance;
        allianceUUID = alliance != null ? alliance.getUUID() : null;
    }

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
    public void setRank(PlayerRank rank) {
        this.rank = rank;
    }
    public ChatChannels getChat() {
        if(chat == null) {
            final String[] chat = yml.getString("chat").split(";");
            this.chat = new ChatChannels(ChatChannel.valueOf(chat[0]), Boolean.parseBoolean(chat[1]), Boolean.parseBoolean(chat[2]), Boolean.parseBoolean(chat[3]), Boolean.parseBoolean(chat[4]), Boolean.parseBoolean(chat[5]), Boolean.parseBoolean(chat[6]));
        }
        return chat;
    }

    public HashMap<Kit, Long> getKitExpirations() {
        if(kitExpirations == null) {
            kitExpirations = new HashMap<>();
            final ConfigurationSection kits = yml.getConfigurationSection("kits");
            if(kits != null) {
                for(String s : kits.getKeys(false)) {
                    kitExpirations.put(Kit.kits.get(s), yml.getLong("kits." + s));
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
    public List<UMaterial> getFilteredItems() {
        if(filteredItems == null) {
            filteredItems = new ArrayList<>();
            for(String s : yml.getStringList("filtered items")) {
                filteredItems.add(UMaterial.valueOf(s));
            }
        }
        return filteredItems;
    }
    public List<Adventure> getAllowedAdventures() {
        if(allowedAdventures == null) {
            allowedAdventures = new ArrayList<>();
            for(String s : yml.getStringList("allowed adventures")) {
                final Identifiable i = RSStorage.get(Feature.ADVENTURE, s);
                if(i != null) {
                    allowedAdventures.add((Adventure) i);
                }
            }
        }
        return allowedAdventures;
    }

    private void save() {
        try {
            yml.save(file);
            yml = YamlConfiguration.loadConfiguration(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPlayerSkillLevel(PlayerSkill skill) {
        for(ActivePlayerSkill a : skills) {
            if(a.type.equals(skill)) {
                return a.level;
            }
        }
        return 0;
    }
}
