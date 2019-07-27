package me.randomhashtags.randomsky.utils;

import me.randomhashtags.randomsky.RandomSky;
import me.randomhashtags.randomsky.addons.Adventure;
import me.randomhashtags.randomsky.addons.Alliance;
import me.randomhashtags.randomsky.addons.Island;
import me.randomhashtags.randomsky.addons.PlayerRank;
import me.randomhashtags.randomsky.addons.active.Home;
import me.randomhashtags.randomsky.utils.universal.UMaterial;
import me.randomhashtags.randomsky.utils.universal.UVersion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RSPlayer {
    private static final String s = File.separator, folder = RandomSky.getPlugin.getDataFolder() + s + "_Data" + s + "players";
    private static final UVersion uv = UVersion.getUVersion();
    public static final HashMap<UUID, RSPlayer> players = new HashMap<>();

    private boolean isLoaded = false;
    private UUID uuid;
    private File file = null;
    private YamlConfiguration yml = null;
    private Island island = null;
    private Alliance alliance = null;
    private UUID islandUUID = null, allianceUUID = null;
    private ChatChannels chat = null;
    private PlayerRank rank = null;

    public long canDeleteIslandTime = 0, coinflipWonCash = 0, coinflipLostCash = 0, coinflipTaxesPaid = 0, jackpotWonCash = 0;
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            file = new File(folder, uuid.toString() + ".yml");
            yml = YamlConfiguration.loadConfiguration(file);
            players.put(uuid, this);
        }
        if(backup) backup();
    }
    public static RSPlayer get(UUID player) { return players.getOrDefault(player, new RSPlayer(player)); }
    public void backup() {
        yml.set("name", Bukkit.getOfflinePlayer(uuid).getName());
        yml.set("rank", getRank() != null ? rank.path : "null");
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
        final List<String> homez = new ArrayList<>(), filtered = new ArrayList<>(), adv = new ArrayList<>();
        for(Home h : getHomes()) homez.add(h.name + ";" + uv.toString(h.location));
        yml.set("homes", homez);
        for(UMaterial u : getFilteredItems()) filtered.add(u.name());
        yml.set("filtered items", filtered);
        for(Adventure a : getAllowedAdventures()) adv.add(a.getYamlName());
        yml.set("allowed adventures", adv);
        save();
    }
    public void load() {
        if(!isLoaded) {
            isLoaded = true;

            final String[] booleans = yml.getString("booleans").split(";"), ints = yml.getString("ints").split(";"), longs = yml.getString("longs").split(";");
            final String U = yml.getString("island"), UU = yml.getString("alliance"), R = yml.getString("rank");
            if(R != null && !R.equals("null")) {
                rank = PlayerRank.paths.get(R);
            }
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
    }
    public void unload() {
        if(isLoaded) {
            isLoaded = false;
            file = null;
            yml = null;
            setIsland(null);
            setAlliance(null);
            chat = null;
            rank = null;
            auctions = null;
            allowedAdventures = null;
            skills = null;
            filteredItems = null;
            kitExpirations = null;
            players.remove(uuid);
            uuid = null;
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
            final String R = yml.getString("rank");
            if(R != null && !R.equals("null")) {
                rank = PlayerRank.paths.get(R);
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
            for(String s : yml.getStringList("homes")) {
                final String name = s.split(";")[0];
                homes.add(new Home(name, uv.toLocation(s.substring(name.length()+1))));
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
                final Adventure a = Adventure.adventures.getOrDefault(s, null);
                if(a != null) allowedAdventures.add(a);
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
        for(ActivePlayerSkill a : skills)
            if(a.type.equals(skill))
                return a.level;
        return 0;
    }
}
