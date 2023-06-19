package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.RSPlayer;
import me.randomhashtags.randomsky.addon.InviteType;
import me.randomhashtags.randomsky.addon.file.FileIsland;
import me.randomhashtags.randomsky.addon.file.FileIslandOrigin;
import me.randomhashtags.randomsky.addon.file.FileIslandRole;
import me.randomhashtags.randomsky.addon.island.*;
import me.randomhashtags.randomsky.addon.PermissionBlock;
import me.randomhashtags.randomsky.addon.active.ActivePermissionBlock;
import me.randomhashtags.randomsky.addon.obj.RSInvite;
import me.randomhashtags.randomsky.api.skill.IslandFarming;
import me.randomhashtags.randomsky.api.skill.IslandMining;
import me.randomhashtags.randomsky.api.skill.IslandSlayer;
import me.randomhashtags.randomsky.event.InviteExpireEvent;
import me.randomhashtags.randomsky.event.island.IslandBreakBlockEvent;
import me.randomhashtags.randomsky.event.island.IslandPlaceBlockEvent;
import me.randomhashtags.randomsky.util.*;
import me.randomhashtags.randomsky.universal.UInventory;
import me.randomhashtags.randomsky.universal.UMaterial;
import org.bukkit.*;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.io.File.separator;

public enum Islands implements IslandAddon, CommandExecutor, Schematicable {
    INSTANCE;

    public static YamlConfiguration config;
    private Location spawn;

    public static String islandWorld;
    private String worldeditF;
    public int distanceBetweenIslands = 0;
    private int deletionDelay = 0;
    private UInventory origin, gui, confirmDelete, members;
    private ItemStack deleteConfirm, deleteCancel, membersBack;
    private boolean falldmg, lavadmg, firedmg, drowningdmg, pvpdmg, magmaBlockdmg;

    private List<String> originSelected, viewingMembers;
    private List<Player> pickingOrigin, managing;
    private List<Location> recentlyDeleted;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final String c = cmd.getName();
        final int l = args.length;
        if(c.equals("island")) {
            if(player != null) {
                boolean j = false;
                if(l == 0) {
                    zeroArgument(player);
                } else if(l == 1) {
                    final String a = args[0];
                    if(a.equals("setspawn") && hasPermission(player, "RandomSky.island.setspawn", true)) {
                        setSpawnLocation(player.getLocation());
                    } else if(a.equals("create")) {
                        tryCreating(player);
                    } else if(a.equals("createworld") && hasPermission(player, "RandomSky.createworld", true)) {
                        createIslandWorld();
                    } else if(a.equals("home")) {
                        tryGoingHome(player);
                    } else if(a.equals("sethome")) {
                        trySettingHome(player);
                    } else if(a.equals("join")) {
                        sendStringListMessage(player, getStringList(config, "messages.join usage"), null);
                    } else if(a.equals("members")) {
                        viewMembers(player);
                    } else if(a.equals("delete")) {
                        tryDeleting(player);
                    } else if(a.equals("kick")) {
                        sendStringListMessage(player, getStringList(config, "messages.kick usage"), null);
                    } else if(a.equals("remove")) {
                        sendStringListMessage(player, getStringList(config, "messages.remove usage"), null);
                    } else if(a.equals("add") || a.equals("invite")) {
                        sendStringListMessage(player, getStringList(config, "messages.invite usage"), null);
                    } else if(a.equals("ban")) {
                        sendStringListMessage(player, getStringList(config, "messages.ban usage"), null);
                    } else if(a.equals("unban")) {
                        sendStringListMessage(player, getStringList(config, "messages.unban usage"), null);
                    } else if(a.equals("warp")) {
                        sendStringListMessage(player, getStringList(config, "messages.warp usage"), null);
                    } else if(a.equals("list")) {

                    } else if(a.equals("close")) {
                        tryClosing(player);
                    } else if(a.equals("open")) {
                        tryOpeningToPublic(player);
                    } else if(a.equals("top")) {

                    } else if(a.equals("origin") || a.equals("origins")) {
                        viewOrigins(player, false);
                    } else if(a.equals("setwarp")) {
                        trySettingWarp(player);
                    } else if(a.equals("delwarp")) {
                        tryDeletingWarp(player);
                    } else if(a.equals("farming")) {
                        final IslandFarming f = IslandFarming.INSTANCE;
                        if(f.is_enabled()) {
                            f.viewFarming(player);
                        }
                    } else if(a.equals("level")) {
                        final IslandLevels levels = IslandLevels.INSTANCE;
                        if(levels.is_enabled()) {
                            levels.viewLevels(player);
                        }
                    } else if(a.equals("mining")) {
                        final IslandMining mining = IslandMining.INSTANCE;
                        if(mining.is_enabled()) {
                            mining.viewMining(player);
                        }
                    } else if(a.equals("slayer")) {
                        final IslandSlayer slayer = IslandSlayer.getIslandSlayer();
                        if(slayer.is_enabled()) {
                            slayer.viewSlayer(player);
                        }
                    } else if(a.equals("challenge") || a.equals("challenges")) {
                        final IslandChallenges challenges = IslandChallenges.getIslandChallenges();
                        if(challenges.is_enabled()) {
                            challenges.viewChallenges(player);
                        }
                    } else if(!a.equals("help")) {
                        j = true;
                    }
                } else if(l == 2) {
                    final String a = args[0], b = args[1];
                    final OfflinePlayer o = !a.equals("open") && !a.equals("close") ? Bukkit.getOfflinePlayer(b) : null;
                    if(a.equals("add") || a.equals("invite")) {
                        tryInviting(player, o);
                    } else if(a.equals("join")) {
                        tryJoining(player, b);
                    } else if(a.equals("remove")) {

                    } else if(a.equals("ban")) {
                        tryBanning(player, o);
                    } else if(a.equals("unban")) {
                        tryUnbanning(player, o);
                    } else if(a.equals("kick")) {
                        tryKicking(player, o);
                    } else if(a.equals("visit") || a.equals("tp") || a.equals("teleport") || a.equals("warp")) {
                        tryWarping(player, b);
                    } else if(a.equals("open")) {
                    } else {
                        j = true;
                    }
                }
                if(j) {
                    viewManagement(player);
                }
            }
            if(l == 1 && args[0].equals("help")) {
                viewHelp(sender);
            }
        } else if(c.equals("origin") && player != null) {
            if(hasPermission(player, "RandomSky.origins", true)) {
                viewOrigins(player, false);
            }
        }
        return true;
    }

    @Override
    public @NotNull Feature get_feature() {
        return Feature.ISLAND_ORIGIN;
    }

    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        final String folder = DATA_FOLDER + separator + "island";
        save(folder, "_settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(folder, "_settings.yml"));

        pickingOrigin = new ArrayList<>();
        managing = new ArrayList<>();
        recentlyDeleted = new ArrayList<>();

        SCHEDULER.runTaskAsynchronously(RANDOM_SKY, () -> {
            final String s = otherdata.getString("spawn");
            if(s != null && !s.equals("null")) {
                spawn = toLocation(s);
            }
        });

        islandWorld = config.getString("island.world");
        distanceBetweenIslands = config.getInt("island.distance between islands");

        deletionDelay = config.getInt("island.deletion delay");
        falldmg = config.getBoolean("island.allowed damage.fall");
        lavadmg = config.getBoolean("island.allowed damage.lava");
        drowningdmg = config.getBoolean("island.allowed damage.drowning");
        pvpdmg = config.getBoolean("island.allowed damage.pvp");

        originSelected = getStringList(config, "origins.settings.selected");
        origin = new UInventory(null, config.getInt("origins.gui.size"), colorize(config.getString("origins.gui.title")));
        final Inventory oi = origin.getInventory();
        final ItemStack b = d(config, "origins.gui.background");
        int origins = 0;
        final Plugin worldEdit = PLUGIN_MANAGER.getPlugin("WorldEdit");
        worldeditF = worldEdit.getDataFolder() + separator + "schematics";

        for(File f : new File(folder + separator + "origins").listFiles()) {
            final FileIslandOrigin o = new FileIslandOrigin(f);
            oi.setItem(o.getSlot(), o.getItem());
        }

        for(int i = 0; i < origin.getSize(); i++) {
            if(oi.getItem(i) == null) {
                oi.setItem(i, b);
            }
        }

        confirmDelete = new UInventory(null, config.getInt("delete.size"), colorize(config.getString("delete.title")));
        final Inventory cdi = confirmDelete.getInventory();
        deleteConfirm = d(config, "delete.confirm");
        deleteCancel = d(config, "delete.cancel");
        for(String s : config.getConfigurationSection("delete").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("confirm") && !s.equals("cancel")) {
                final String p = "delete." + s + ".";
                final int slot = config.getInt(p + "slot");
                final String it = config.getString(p + "item");
                if(it != null) {
                    final ItemStack item;
                    if(it.toLowerCase().equals("confirm")) {
                        item = deleteConfirm.clone();
                    } else if(it.toLowerCase().equals("cancel")) {
                        item = deleteCancel.clone();
                    } else {
                        item = d(config, "delete." + s);
                    }
                    cdi.setItem(slot, item);
                }
            }
        }
        final String type = config.getString("gui.type"), title = colorize(config.getString("gui.title"));
        final int size = config.getInt("gui.size");
        gui = type != null ? new UInventory(null, InventoryType.valueOf(type.toUpperCase()), title) : new UInventory(null, size, title);

        final Inventory gi = gui.getInventory();
        for(String s : config.getConfigurationSection("gui").getKeys(false)) {
            if(!s.equals("title") && !s.equals("type") && !s.equals("size")) {
                final String p = "gui." + s + ".";
                final int slot = config.getInt(p + "slot");
                gi.setItem(slot, d(config, "gui." + s));
            }
        }


        for(File f : new File(folder + separator + "roles").listFiles()) {
            new FileIslandRole(f);
        }
        defaultMember = config.getString("roles.settings.default member");
        defaultCreator = config.getString("roles.settings.default creator");

        members = new UInventory(null, 54, colorize(config.getString("members.title")));
        membersBack = d(config, "members.back");
        viewingMembers = getStringList(config, "roles.settings.viewing members");

        for(String s : otherdata.getStringList("recently deleted")) {
            recentlyDeleted.add(toLocation(s));
        }
        sendConsoleMessage("&6[RandomSky] &aLoaded Islands and " + origins + " origins &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void setSpawnLocation(@NotNull Location location) {
        this.spawn = location;
    }
    @Override
    public void unload() {
        otherdata.set("spawn", spawn != null ? toString(spawn) : "null");
        final List<String> rd = new ArrayList<>();
        for(Location l : recentlyDeleted) {
            rd.add(toString(l));
        }
        otherdata.set("recently deleted", rd);
        saveOtherData();

        config = null;
        islandWorld = null;

        RSStorage.unregisterAll(Feature.ISLAND_CHALLENGE, Feature.ISLAND_LEVEL, Feature.ISLAND_ORIGIN, Feature.ISLAND_PROGRESSIVE_SKILL, Feature.ISLAND_RANK, Feature.ISLAND_REGION_PROTECTION, Feature.ISLAND_SKILL, Feature.ISLAND_UPGRADE);
    }
    private void createIslandWorld() {
        Bukkit.createWorld(WorldCreator.name(islandWorld).type(WorldType.FLAT).generatorSettings("3;minecraft:air;127;decoration"));
    }
    private void createIsland(@NotNull Player player, @NotNull IslandOrigin origin) {
        final UUID u = player.getUniqueId();
        final Location center = newIslandCenter();
        final Island i = new FileIsland(origin, u, center);
        final FileRSPlayer pdata = FileRSPlayer.get(u);
        pdata.setIslandUUID(i.getUUID());
        player.closeInventory();
        player.sendMessage(ChatColor.YELLOW + "Please wait as your island is being created...");
        try {
            pasteSchematic(origin.getSchematic(), center);
        } catch (Exception e) {
            e.printStackTrace();
        }
        player.teleport(center.clone().add(0.5, 1, 0.5));
        sendStringListMessage(player, getStringList(config, "messages.create"), null);
    }

    public void viewHelp(@NotNull CommandSender sender) {
        if(hasPermission(sender, "RandomSky.island.help", true)) {
            sendStringListMessage(sender, getStringList(config, "messages.help"), null);
        }
    }
    public void viewMembers(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.island.members", true)) {
            final Island is = FileRSPlayer.get(player.getUniqueId()).getIsland();
            if(is == null) {
                sendStringListMessage(player, getStringList(config, "messages.need island"), null);
            } else {
                final HashMap<UUID, IslandRank> m = is.getMembers();
                int size = m.size();
                size = ((size+9)/9)*9;
                player.openInventory(Bukkit.createInventory(player, size, members.getTitle()));
                final Inventory top = player.getOpenInventory().getTopInventory();
                for(UUID uuid : m.keySet()) {
                    final OfflinePlayer OP = Bukkit.getOfflinePlayer(uuid);
                    final IslandRank r = m.get(uuid);
                    final String R = r.getString();
                    final ItemStack item = UMaterial.PLAYER_HEAD_ITEM.getItemStack();
                    final SkullMeta sm = (SkullMeta) item.getItemMeta();
                    sm.setOwningPlayer(OP);
                    sm.setDisplayName(ChatColor.GREEN + OP.getName());
                    final List<String> lore = new ArrayList<>();
                    for(String s : viewingMembers) {
                        lore.add(s.replace("{RANK}", R));
                    }
                    sm.setLore(lore);
                    item.setItemMeta(sm);
                    top.setItem(top.firstEmpty(), item);
                }
                player.updateInventory();
            }
        }
    }
    public void tryCreating(@NotNull Player creator) {
        final Island is = Island.players.getOrDefault(creator.getUniqueId(), null);
        if(is == null) {
            viewOrigins(creator, true);
        } else {
            sendStringListMessage(creator, getStringList(config, "messages.already have an island"), null);
        }
    }
    public void tryInviting(@NotNull Player sender, OfflinePlayer target) {
        if(hasPermission(sender, "RandomSky.island.invite", true)) {
            final UUID s = sender.getUniqueId();
            final FileRSPlayer rs = FileRSPlayer.get(s);
            final Island is = rs.getIsland();
            if(is == null) {
                sendStringListMessage(sender, getStringList(config, "messages.need island"), null);
            } else {
                final UUID r = target.getUniqueId();
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{PLAYER}", target.getName());
                replacements.put("{IS_CREATOR}", Bukkit.getOfflinePlayer(is.getCreator()).getName());
                if(s == r) {
                    sendStringListMessage(sender, getStringList(config, "messages.cannot invite self"), null);
                } else if(is.getMembers().containsKey(r)) {
                    sendStringListMessage(sender, getStringList(config, "messages.invite already member"), replacements);
                } else {
                    final List<RSInvite> invites = is.getInvites();
                    for(RSInvite i : invites) {
                        if(i.receiver == r) {
                            sendStringListMessage(sender, getStringList(config, "messages.invite pending"), replacements);
                            return;
                        }
                    }
                    invites.add(new RSInvite(System.currentTimeMillis(), rs, r, InviteType.ISLAND, 60));
                    sendStringListMessage(sender, getStringList(config, "messages.invite sent"), replacements);
                    if(target.isOnline()) {
                        replacements.put("{PLAYER}", sender.getName());
                        sendStringListMessage(target.getPlayer(), getStringList(config, "messages.invite receive"), replacements);
                    }
                }
            }
        }
    }
    public void tryJoining(@NotNull Player player, String target) {
        if(hasPermission(player, "RandomSky.island.join", true) && target != null) {
            final OfflinePlayer creator = Bukkit.getOfflinePlayer(target);
            if(creator != null) {
                final FileRSPlayer rsp = FileRSPlayer.get(creator.getUniqueId());
                final Island island = rsp.getIsland();
                final List<String> noinvitefound = getStringList(config, "messages.no invite to join");
                final HashMap<String, String> replacements = new HashMap<>();
                if(island == null) {
                    replacements.put("{PLAYER}", creator.getName());
                    sendStringListMessage(player, noinvitefound, replacements);
                } else {
                    final UUID u = player.getUniqueId();
                    final List<RSInvite> invites = island.getInvites();
                    for(RSInvite i : invites) {
                        if(i.receiver == u) {
                            SCHEDULER.cancelTask(i.expireTask);
                            replacements.put("{PLAYER}", player.getName());
                            replacements.put("{SENDER}", Bukkit.getOfflinePlayer(i.sender.getUUID()).getName());
                            replacements.put("{IS_CREATOR}", Bukkit.getOfflinePlayer(island.getCreator()).getName());
                            final List<String> msg = getStringList(config, "messages.invite accept");
                            for(Player p : island.getOnlineMembers()) {
                                sendStringListMessage(p, msg, replacements);
                            }
                            island.join(player);
                            return;
                        }
                    }
                    replacements.put("{PLAYER}", creator.getName());
                    sendStringListMessage(player, noinvitefound, replacements);
                }
            }
        }
    }
    public void tryWarping(@NotNull Player player, @NotNull String input) {
        if(hasPermission(player, "RandomSky.island.warp", true)) {
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{INPUT}", input);
            final Player online = Bukkit.getPlayer(input);
            if(online == null) {
                sendStringListMessage(player, getStringList(config, "messages.unable to warp to island"), replacements);
            } else {
                replacements.put("{PLAYER}", online.getName());
                final Island island = Island.players.getOrDefault(online.getUniqueId(), null), on = Island.valueOf(player.getLocation());
                final Location warp = island != null ? island.getWarp() : null;
                if(island == null || warp == null) {
                    sendStringListMessage(player, getStringList(config, "messages.no island to warp to"), replacements);
                } else if(island == on) {
                    sendStringListMessage(player, getStringList(config, "messages.warp already on island"), null);
                } else if(online == player) {
                    sendStringListMessage(player, getStringList(config, "messages.try warp self"), null);
                } else {
                    player.teleport(warp, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    sendStringListMessage(player, getStringList(config, "messages.warp to island"), replacements);
                }
            }
        }
    }
    public void trySettingWarp(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.island.setwarp", true)) {
            final Island is = FileRSPlayer.get(player.getUniqueId()).getIsland();
            if(is == null) {
                sendStringListMessage(player, getStringList(config, "messages.need island"), null);
            } else {
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{PLAYER}", player.getName());
                is.setWarp(player.getLocation());
                sendStringListMessage(player, getStringList(config, "messages.setwarp"), replacements);
            }
        }
    }
    public void tryDeletingWarp(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.island.delwarp", true)) {
            final Island is = FileRSPlayer.get(player.getUniqueId()).getIsland();
            if(is == null) {
                sendStringListMessage(player, getStringList(config, "messages.need island"), null);
            } else {
                final Location w = is.getWarp();
                if(w == null) {
                    sendStringListMessage(player, getStringList(config, "messages.delwarp no warp"), null);
                } else {
                    is.setWarp(null);
                    sendStringListMessage(player, getStringList(config, "messages.delwarp"), null);
                }
            }
        }
    }
    public void tryBanning(@NotNull Player player, @NotNull OfflinePlayer target) {
        if(hasPermission(player, "RandomSky.island.ban", true)) {
            final Island is = hasIsland(player);
            if(is != null) {
                if(player.equals(target.getPlayer())) {
                    sendStringListMessage(player, getStringList(config, "messages.cannot ban self"), null);
                } else {
                    final UUID target_player_uuid = target.getUniqueId();
                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("{PLAYER}", player.getName());
                    replacements.put("{TARGET}", target.getName());
                    replacements.put("{IS_CREATOR}", Bukkit.getOfflinePlayer(is.getCreator()).getName());
                    if(!is.getBannedPlayers().contains(target_player_uuid)) {
                        is.ban(target_player_uuid);
                        sendStringListMessage(player, getStringList(config, "messages.ban"), replacements);
                    } else {
                        sendStringListMessage(player, getStringList(config, "messages.already banned"), replacements);
                    }
                }
            }
        }
    }
    public void tryUnbanning(Player player, OfflinePlayer target) {
        if(hasPermission(player, "RandomSky.island.unban", true)) {
            final Island is = hasIsland(player);
            if(is != null) {
                final UUID u = target.getUniqueId();
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{PLAYER}", player.getName());
                replacements.put("{TARGET}", target.getName());
                replacements.put("{IS_CREATOR}", Bukkit.getOfflinePlayer(is.getCreator()).getName());
                final List<UUID> banned = is.getBannedPlayers();
                if(banned.contains(u)) {
                    banned.remove(u);
                    for(Player p : is.getOnlineMembers()) {
                        sendStringListMessage(p, getStringList(config, "messages.unban"), replacements);
                    }
                } else {
                    sendStringListMessage(player, getStringList(config, "messages.not banned"), replacements);
                }
            }
        }
    }
    public void tryKicking(Player player, OfflinePlayer target) {
        if(hasPermission(player, "RandomSky.island.kick", true)) {
            final Island is = hasIsland(player), t = target.isOnline() ? Island.valueOf(target.getPlayer().getLocation()) : null;
            if(is != null) {
                final String n = target.getName(), p = player.getName();
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{TARGET}", n);
                replacements.put("{PLAYER}", p);
                replacements.put("{IS_CREATOR}", Bukkit.getOfflinePlayer(is.getCreator()).getName());
                if(n.equals(p)) {
                    sendStringListMessage(player, getStringList(config, "messages.cannot kick self"), null);
                } else if(t == null) {
                    sendStringListMessage(player, getStringList(config, "messages.kick not on island"), replacements);
                } else {
                    final Player tar = target.getPlayer();
                    tar.teleport(spawn, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    sendStringListMessage(tar, getStringList(config, "messages.been kicked"), replacements);
                    sendStringListMessage(player, getStringList(config, "messages.kicked"), replacements);
                    for(Player P : is.getOnlineMembers()) {
                        sendStringListMessage(P, getStringList(config, "messages.kicked notify"), replacements);
                    }
                }
            }
        }
    }
    private Island hasIsland(Player player) {
        final Island is = Island.players.getOrDefault(player.getUniqueId(), null);
        if(is == null) {
            sendStringListMessage(player, getStringList(config, "messages.need island"), null);
        }
        return is;
    }
    public void tryOpeningToPublic(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.island.open", true)) {
            final Island is = hasIsland(player);
            if(is != null) {
                final boolean isOpen = is.isOpenToPublic();
                if(isOpen) {
                    sendStringListMessage(player, getStringList(config, "messages.island already open to public"), null);
                } else {
                    is.setOpenToPublic(true);
                    sendStringListMessage(player, getStringList(config, "messages.island open to public"), null);
                }
            }
        }
    }
    public void tryClosing(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.island.close", true)) {
            final Island is = hasIsland(player);
            if(is != null) {
                final boolean isOpen = is.isOpenToPublic();
                if(!isOpen) {
                    sendStringListMessage(player, getStringList(config, "messages.your island is not open to the public"), null);
                } else {
                    is.setOpenToPublic(false);
                    sendStringListMessage(player, getStringList(config, "messages.no longer open to public"), null);
                }
            }
        }
    }
    private void viewOrigins(Player player, boolean pick) {
        final FileRSPlayer pdata = FileRSPlayer.get(player.getUniqueId());
        player.closeInventory();
        final int size = origin.getSize();
        player.openInventory(Bukkit.createInventory(player, size, origin.getTitle()));
        final Inventory top = player.getOpenInventory().getTopInventory();
        top.setContents(origin.getInventory().getContents());
        final Island is = pdata.getIsland();
        if(is != null) {
            final IslandOrigin O = is.getOrigin();
            for(int i = 0; i < size; i++) {
                final IslandOrigin o = IslandOrigin.valueOf(i);
                if(o != null && o.equals(O)) {
                    final ItemStack item = top.getItem(i);
                    final ItemMeta itemMeta = item.getItemMeta();
                    final List<String> lore = new ArrayList<>(itemMeta.getLore());
                    lore.addAll(originSelected);
                    itemMeta.setLore(lore);
                    item.setItemMeta(itemMeta);
                }
            }
        }
        player.updateInventory();
        if(pick) pickingOrigin.add(player);
    }
    public void zeroArgument(@NotNull Player player) {
        final Island is = Island.players.getOrDefault(player.getUniqueId(), null);
        if(is == null) {
            sendStringListMessage(player, getStringList(config, "messages.zero arguments"), null);
        } else {
            viewManagement(player);
        }
    }
    public void viewManagement(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.island.manage", true)) {
            final Island is = hasIsland(player);
            if(is != null) {
                player.closeInventory();
                final int size = gui.getSize();
                player.openInventory(Bukkit.createInventory(player, gui.getType(), gui.getTitle().replace("{PLAYER}", player.getName())));
                final Inventory top = player.getOpenInventory().getTopInventory();
                top.setContents(gui.getInventory().getContents());
                managing.add(player);
                final String islandLevel = Integer.toString(is.getIslandLevel().getLevel()), origin = is.getOrigin().getName(), radius = Integer.toString(is.radius), farmingLevel = Integer.toString(is.farmingSkill.level), slayerLevel = Integer.toString(is.slayerSkill.level), miningLevel = Integer.toString(is.allowedNodes.size()), rnValue = formatDouble(is.getResourceNodeValue());
                for(int i = 0; i < size; i++) {
                    ItemStack item = top.getItem(i);
                    if(item != null) {
                        item = item.clone();
                        ItemMeta itemMeta = item.getItemMeta();
                        final List<String> lore = new ArrayList<>();
                        if(itemMeta.hasLore()) {
                            for(String s : itemMeta.getLore()) {
                                lore.add(s.replace("{LEVEL}", islandLevel).replace("{ORIGIN}", origin).replace("{RADIUS}", radius).replace("{FARMING_LEVEL}", farmingLevel).replace("{SLAYER_LEVEL}", slayerLevel).replace("{MINING_LEVEL}", miningLevel).replace("{RESOURCE_NODE_VALUE}", rnValue));
                            }
                        }
                        itemMeta.setLore(lore);
                        item.setItemMeta(itemMeta);
                        top.setItem(i, item);
                    }
                }
                player.updateInventory();
            }
        }
    }
    private int getRandomLong(int digits, boolean negative) {
        String p = negative ? "-" : "";
        for(int i = 1; i <= digits; i++) {
            p = p + RANDOM.nextInt(10);
        }
        return Integer.parseInt(p);
    }
    private Location getRandomIslandLocation(World w) {
        final int x = getRandomLong(1+ RANDOM.nextInt(7), RANDOM.nextInt(2) == 0), z = getRandomLong(1+ RANDOM.nextInt(7), RANDOM.nextInt(2) == 0);
        return new Location(w, x, 100, z);
    }
    private Location newIslandCenter() {
        if(!recentlyDeleted.isEmpty()) {
            final Location l = recentlyDeleted.get(RANDOM.nextInt(recentlyDeleted.size()));
            recentlyDeleted.remove(l);
            return l;
        } else {
            final World w = Bukkit.getWorld(islandWorld);
            Island is = null;
            while(is == null) {
                final Location l = getRandomIslandLocation(w);
                is = Island.valueOf(l);
                if(is == null) {
                    return l;
                }
            }
            return null;
        }
    }
    public void tryDeleting(@NotNull Player player) {
        if(player != null && hasPermission(player, "RandomSky.island.delete", true)) {
            final UUID u = player.getUniqueId();
            final FileRSPlayer pdata = FileRSPlayer.get(u);
            final Island is = hasIsland(player);
            if(is == null) {
                return;
            }
            final long l = System.currentTimeMillis(), last = pdata.canDeleteIslandTime;
            if(l < last && !hasPermission(player, "RandomSky.island.delete.bypasstimer", false)) {
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{TIME}", getRemainingTime(last-l));
                sendStringListMessage(player, getStringList(config, "messages.delete must wait"), replacements);
            } else {
                player.openInventory(Bukkit.createInventory(player, confirmDelete.getSize(), confirmDelete.getTitle()));
                final Inventory top = player.getOpenInventory().getTopInventory();
                top.setContents(confirmDelete.getInventory().getContents());
                player.updateInventory();
            }
        }
    }
    private void delete(Player player, FileRSPlayer pdata, Island island) {
        final Location C = island.getCenter();
        final World w = C.getWorld();
        final int r = island.radius, x = C.getBlockX(), z = C.getBlockZ();
        island.delete();
        player.closeInventory();
        player.teleport(spawn);
        pdata.setIslandUUID(null);
        pdata.canDeleteIslandTime = System.currentTimeMillis()+deletionDelay*1000;
        sendStringListMessage(player, getStringList(config, "messages.delete"), null);

        SCHEDULER.runTaskAsynchronously(RANDOM_SKY, () -> {
            try {
                final List<Chunk> chunks = new ArrayList<>();
                for(int X = x; X <= x+r; X++) {
                    for(int Z = z; Z <= z+r; Z++) {
                        final Chunk c = w.getBlockAt(X, 0, Z).getChunk();
                        if(!chunks.contains(c)) chunks.add(c);
                    }
                    for(int Z = z; Z >= z-r; Z--) {
                        final Chunk c = w.getBlockAt(X, 0, Z).getChunk();
                        if(!chunks.contains(c)) chunks.add(c);
                    }
                }
                for(int X = x; X >= x-r; X--) {
                    for(int Z = z; Z <= z+r; Z++) {
                        final Chunk c = w.getBlockAt(X, 0, Z).getChunk();
                        if(!chunks.contains(c)) chunks.add(c);
                    }
                    for(int Z = z; Z >= z-r; Z--) {
                        final Chunk c = w.getBlockAt(X, 0, Z).getChunk();
                        if(!chunks.contains(c)) chunks.add(c);
                    }
                }
                for(Chunk c : chunks) {
                    cleanChunk(c);
                }
                recentlyDeleted.add(C);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    public void tryGoingHome(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.island.home", true)) {
            final Island is = Island.players.getOrDefault(player.getUniqueId(), null);
            if(is == null) {
                sendStringListMessage(player, getStringList(config, "messages.home doesnt have island"), null);
            } else {
                player.teleport(is.getHome());
            }
        }
    }
    public void trySettingHome(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.island.sethome", true)) {
            final Island is = hasIsland(player);
            if(is != null) {
                final Location l = player.getLocation();
                final Island target = Island.valueOf(l);
                if(target == is) {
                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("{X}", Integer.toString(l.getBlockX()));
                    replacements.put("{Y}", Integer.toString(l.getBlockY()));
                    replacements.put("{Z}", Integer.toString(l.getBlockZ()));
                    is.setHome(l);
                    sendStringListMessage(player, getStringList(config, "messages.sethome"), replacements);
                }
            }
        }
    }
    private void dmgDurability(ItemStack is) {
        if(is != null) {
            final String s = is.getType().name();
            if(RANDOM.nextInt(100) < 55 && (s.contains("AXE") || s.contains("SWORD") || s.contains("HOE") || s.contains("SPADE") || s.contains("SHOVEL"))) {
                is.setDurability((short) (is.getDurability()+1));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void blockPlaceEvent(BlockPlaceEvent event) {
        final Block b = event.getBlockPlaced();
        final World w = b.getWorld();
        if(w.getName().equals(islandWorld)) {
            final Location l = b.getLocation();
            final Player player = event.getPlayer();
            final UUID u = player.getUniqueId();
            final Island is = Island.valueOf(l);
            if(is != null) {
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{IS_CREATOR}", Bukkit.getOfflinePlayer(is.getCreator()).getName());
                if(!is.getMembers().containsKey(u)) {
                    event.setCancelled(true);
                    sendStringListMessage(player, getStringList(config, "messages.cannot build or break on island"), replacements);
                } else {
                    final ItemStack i = event.getItemInHand();
                    final PermissionBlock pb = PermissionBlock.valueOf(i);
                    if(pb != null) {
                        is.getPermissionBlocks().add(new ActivePermissionBlock(l, pb));
                    } else {
                        final IslandPlaceBlockEvent e = new IslandPlaceBlockEvent(player, is, i, b);
                        PLUGIN_MANAGER.callEvent(e);
                        event.setCancelled(e.isCancelled());
                    }
                }
            } else {
                event.setCancelled(true);
                sendStringListMessage(player, getStringList(config, "messages.need to upgrade island radius to place block there"), null);
            }
            player.updateInventory();
        }
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void blockBreakEvent(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final World w = player.getWorld();
        if(w.getName().equals(islandWorld)) {
            final Block b = event.getBlock();
            final Location l = b.getLocation();
            final Island is = Island.valueOf(b.getLocation());
            if(is != null) {
                final UUID uuid = player.getUniqueId();
                if(is.getMembers().containsKey(uuid)) {
                    final IslandBreakBlockEvent e = new IslandBreakBlockEvent(event, player.getItemInHand(), is);
                    PLUGIN_MANAGER.callEvent(e);
                    final boolean cancelled = e.isCancelled();
                    event.setCancelled(cancelled);
                    if(!cancelled) {
                        final FileRSPlayer pdata = FileRSPlayer.get(uuid);
                        boolean did = false, instant = pdata.getToggles().get(ToggleType.INSTANT_BLOCK_PICKUP);
                        final MaterialData md = b.getState().getData();
                        if(md instanceof Crops) {
                            final String cs = md.toString();
                            for(ItemStack its : b.getDrops()) {
                                if(instant) {
                                    giveItem(player, its);
                                } else {
                                    w.dropItemNaturally(l, its);
                                }
                            }
                            final UMaterial u = farming.fromBlock(md.toString());
                            int amount = 1;
                            if(u != null) {
                                final ItemStack seed = u.getItemStack();
                                final boolean ripe = cs.contains("RIPE");
                                if(cs.contains("CROPS") || cs.contains("BEETROOT")) {
                                    amount = ripe ? RANDOM.nextInt(4) : 1;
                                } else if(cs.contains("POTATO") || cs.contains("CARROT")) {
                                    amount = ripe ? 1+ RANDOM.nextInt(4) : 1;
                                } else if(cs.contains("NETHER_WARTS")) {
                                    amount = ripe ? 2+ RANDOM.nextInt(3) : 1;
                                }
                                seed.setAmount(amount);
                                if(instant) {
                                    giveItem(player, seed);
                                } else {
                                    w.dropItemNaturally(l, seed);
                                }
                            }
                            did = true;
                        }
                        event.setDropItems(false);
                        if(!did) {
                            IslandMining.INSTANCE.breakBlock(player, pdata, b);
                        }
                        dmgDurability(player.getItemInHand());
                        player.updateInventory();
                    }
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.LOWEST)
    private void playerInteractEvent(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final World w = player.getWorld();
        if(w.getName().equals(islandWorld)) {
            final Block b = event.getClickedBlock();
            final String action = event.getAction().name();
            final Island is = Island.valueOf(b != null ? b.getLocation() : player.getLocation());
            if(event.getHand() == EquipmentSlot.HAND && b != null && is != null) {
                final PlayerIslandInteractEvent e = new PlayerIslandInteractEvent(player, is, event);
                PLUGIN_MANAGER.callEvent(e);
                event.setCancelled(e.isCancelled());
                if(e.isCancelled()) {
                    return;
                }
            } else if(action.equals("PHYSICAL") && is != null) {
                event.setCancelled(true);
            } else {
                return;
            }
        } else {
            return;
        }
        player.updateInventory();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
        final Player player = event.getPlayer();
        final FileRSPlayer pdata = FileRSPlayer.get(player.getUniqueId());
        final Block b = event.getBlockClicked();
        final Location L = b.getLocation();
        final BlockFace bf = event.getBlockFace();
        final int x = bf.equals(BlockFace.EAST) ? 1 : bf.equals(BlockFace.WEST) ? -1 : 0, z = bf.equals(BlockFace.SOUTH) ? 1 : bf.equals(BlockFace.NORTH) ? -1 : 0;
        final Location l = b.getWorld().getBlockAt(L.getBlockX()+x, L.getBlockY(), L.getBlockZ()+z).getLocation();
        final Island is = Island.valueOf(l);
        if(is == null) {
            sendStringListMessage(player, getStringList(config, "messages.need to upgrade island radius to place block there"), null);
        } else if(is != pdata.getIsland()) {
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{OWNER}", Bukkit.getOfflinePlayer(is.getCreator()).getName());
            sendStringListMessage(player, getStringList(config, "messages.cannot build or break on island"), replacements);
        } else return;
        event.setCancelled(true);
        player.updateInventory();
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerBucketFillEvent(PlayerBucketFillEvent event) {
        final Player player = event.getPlayer();
        final FileRSPlayer pdata = FileRSPlayer.get(player.getUniqueId());
        final Location l = event.getBlockClicked().getLocation();
        final Island is = Island.valueOf(l);
        if(is == null) {
            sendStringListMessage(player, getStringList(config, "messages.need to upgrade island radius to place block there"), null);
        } else if(is != pdata.getIsland()) {
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{OWNER}", Bukkit.getOfflinePlayer(is.getCreator()).getName());
            sendStringListMessage(player, getStringList(config, "messages.cannot build or break on island"), replacements);
        } else return;
        event.setCancelled(true);
        player.updateInventory();
    }


    //@EventHandler(priority = EventPriority.LOWEST)
    private void playerChatEvent(AsyncPlayerChatEvent event) {
        if(!event.isCancelled()) {
            final Player player = event.getPlayer();
            final FileRSPlayer pdata = FileRSPlayer.get(player.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void potionSplashEvent(PotionSplashEvent event) {
        final List<PotionEffectType> types = new ArrayList<>();
        for(PotionEffect pe : event.getPotion().getEffects()) {
            types.add(pe.getType());
        }
        final Collection<LivingEntity> a = event.getAffectedEntities();
        for(int p = 0; p < a.size(); p++) {
            final LivingEntity l = (LivingEntity) a.toArray()[p];
            if(l instanceof Player) {
                final FileRSPlayer pdata = FileRSPlayer.get(l.getUniqueId());
                final Island is = pdata.getIsland();
                if(is != null) {
                    for(PotionEffectType t : is.getImmuneTo()) {
                        if(types.contains(t)) {
                            event.setIntensity(l, 0);
                            break;
                        }
                    }
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityDeathEvent(EntityDeathEvent event) {
        final Player player = event.getEntity().getKiller();
        if(player != null) {
            final Island is = FileRSPlayer.get(player.getUniqueId()).getIsland();
            if(is != null) {
                event.setDroppedExp((int) (event.getDroppedExp() * is.XPGainMultiplier));
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        final Entity e = event.getEntity(), dam = event.getDamager();
        final Player damager = dam instanceof Player ? (Player) dam : null;
        if(damager != null) {
            final UUID u = damager.getUniqueId();
            final Island is = Island.valueOf(e.getLocation());
            if(is != null && e instanceof LivingEntity && !(e instanceof Player)) {
                final boolean own = is.getMembers().containsKey(u);
                if(!own) {
                    event.setCancelled(true);
                } else {

                }
                damager.updateInventory();
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void foodLevelChangeEvent(FoodLevelChangeEvent event) {
        final Player player = (Player) event.getEntity();
        final World w = player.getWorld();
        if(w.getName().equals(islandWorld)) {
            final int f = event.getFoodLevel(), ff = player.getFoodLevel();
            if(ff > f) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void entityDamageEvent(EntityDamageEvent event) {
        final Entity e = event.getEntity();
        final World w = e.getWorld();
        if(w.getName().equals(islandWorld)) {
            final Player victim = e instanceof Player ? (Player) e : null;
            final String c = event.getCause().name();
            if(victim != null) {
                final Island island = FileRSPlayer.get(victim.getUniqueId()).getIsland();
                if(c.contains("FIRE") && !firedmg || c.equals("DROWNING") && !drowningdmg || c.equals("LAVA") && !lavadmg || c.equals("FALL") && !falldmg || c.equals("HOT_FLOOR") && !magmaBlockdmg) {
                    event.setCancelled(true);
                } else if(c.equals("VOID")) {
                    event.setCancelled(true);
                    if(island != null) {
                        victim.teleport(island.getHome());
                    } else {
                        victim.teleport(spawn);
                    }
                    sendStringListMessage(victim, getStringList(config, "messages.fallen into the void"), null);
                } else if(island != null) {
                    final List<PotionEffectType> immune = island.getImmuneTo();
                    for(PotionEffectType t : immune) {
                        if(t.getName().equals(c)) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        if(top.getHolder() == player) {
            final String t = event.getView().getTitle();
            final int r = event.getRawSlot();
            final ItemStack current = event.getCurrentItem();
            final boolean origin = t.equals(this.origin.getTitle()), confirmDelete = t.equals(this.confirmDelete.getTitle()), managing = this.managing.contains(player);
            if(origin || confirmDelete || managing) {
                event.setCancelled(true);
                player.updateInventory();
                if(r < 0 || r >= top.getSize() || current == null || current.getType().equals(Material.AIR)) return;
                if(origin) {
                    final IslandOrigin o = IslandOrigin.valueOf(r);
                    if(o != null) {
                        if(pickingOrigin.contains(player)) {
                            createIsland(player, o);
                            return;
                        } else {
                            sendStringListMessage(player, getStringList(config, "messages.already created origin"), null);
                        }
                    }
                } else if(confirmDelete) {
                    if(current.equals(deleteConfirm)) {
                        final FileRSPlayer pdata = FileRSPlayer.get(player.getUniqueId());
                        delete(player, pdata, pdata.getIsland());
                    } else if(current.equals(deleteCancel)) {
                        player.closeInventory();
                    }
                } else if(managing) {

                }
                player.updateInventory();
            }
        }
    }
    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        pickingOrigin.remove(player);
        managing.remove(player);
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerPickupItemEvent(PlayerPickupItemEvent event) {
        final Player player = event.getPlayer();
        final Location l = player.getLocation();
        if(l.getWorld().getName().equals(islandWorld)) {
            final Island is = Island.valueOf(l);
            if(is != null) {
                if(!is.getMembers().containsKey(player.getUniqueId())) {
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.LOWEST)
    private void inviteExpireEvent(InviteExpireEvent event) {
        final RSInvite i = event.invite;
        final RSPlayer rs = i.sender;
        final Island is = rs.getIsland();
        final OfflinePlayer op = Bukkit.getOfflinePlayer(rs.getUUID());
        if(is != null) {
            is.getInvites().remove(i);
            if(op.isOnline()) {
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{PLAYER}", Bukkit.getOfflinePlayer(i.receiver).getName());
                sendStringListMessage(op.getPlayer(), getStringList(config, "messages.invite expired"), replacements);
            }
        }
    }
    @EventHandler
    private void chunkLoadEvent(ChunkLoadEvent event) {
        if(event.getWorld().getName().equals(islandWorld)) {
            final Location l = event.getChunk().getBlock(0, 0, 0).getLocation();
            final Island island = Island.valueOf(l);
            if(island != null && !island.isLoaded()) {
                island.load();
            }
        }
    }
}
