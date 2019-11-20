package me.randomhashtags.randomsky.api;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.world.registry.WorldData;
import com.sun.istack.internal.NotNull;
import me.randomhashtags.randomsky.addon.file.FileIsland;
import me.randomhashtags.randomsky.addon.file.FileIslandOrigin;
import me.randomhashtags.randomsky.addon.island.*;
import me.randomhashtags.randomsky.addon.PermissionBlock;
import me.randomhashtags.randomsky.addon.ResourceNode;
import me.randomhashtags.randomsky.addon.active.ActivePermissionBlock;
import me.randomhashtags.randomsky.addon.active.ActiveResourceNode;
import me.randomhashtags.randomsky.api.skill.IslandFarming;
import me.randomhashtags.randomsky.api.skill.IslandMining;
import me.randomhashtags.randomsky.event.island.IslandBreakBlockEvent;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSPlayer;
import me.randomhashtags.randomsky.util.RSStorage;
import me.randomhashtags.randomsky.util.universal.UInventory;
import me.randomhashtags.randomsky.util.universal.UMaterial;
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
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static java.io.File.separator;

public class Islands extends IslandAddon implements CommandExecutor {
    private static Islands instance;
    public static Islands getIslands() {
        if(instance == null) instance = new Islands();
        return instance;
    }

    public static YamlConfiguration config;
    public static IslandMining mining;
    private IslandFarming farming;
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
                        sendStringListMessage(player, config.getStringList("messages.join usage"), null);
                    } else if(a.equals("members")) {
                        viewMembers(player);
                    } else if(a.equals("delete")) {
                        tryDeleting(player);
                    } else if(a.equals("kick")) {
                        sendStringListMessage(player, config.getStringList("messages.kick usage"), null);
                    } else if(a.equals("remove")) {
                        sendStringListMessage(player, config.getStringList("messages.remove usage"), null);
                    } else if(a.equals("add") || a.equals("invite")) {
                        sendStringListMessage(player, config.getStringList("messages.invite usage"), null);
                    } else if(a.equals("ban")) {
                        sendStringListMessage(player, config.getStringList("messages.ban usage"), null);
                    } else if(a.equals("unban")) {
                        sendStringListMessage(player, config.getStringList("messages.unban usage"), null);
                    } else if(a.equals("warp")) {
                        sendStringListMessage(player, config.getStringList("messages.warp usage"), null);
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
                        final IslandFarming f = IslandFarming.getIslandFarming();
                        if(f.isEnabled) {
                            f.viewFarming(player);
                        }
                    } else if(a.equals("level")) {
                        final IslandLevels levels = IslandLevels.getIslandLevels();
                        if(levels.isEnabled) {
                            levels.viewLevels(player);
                        }
                    } else if(a.equals("mining")) {
                        final IslandMining mining = IslandMining.getIslandMining();
                        if(mining.isEnabled) {
                            mining.viewMining(player);
                        }
                    } else if(a.equals("slayer")) {
                        final IslandSlayer slayer = IslandSlayer.getIslandSlayer();
                        if(slayer.isEnabled) {
                            slayer.viewSlayer(player);
                        }
                    } else if(a.equals("challenge") || a.equals("challenges")) {
                        final IslandChallenges challenges = IslandChallenges.getIslandChallenges();
                        if(challenges.isEnabled) {
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

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "island settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(randomsky.getDataFolder(), "island settings.yml"));

        pickingOrigin = new ArrayList<>();
        managing = new ArrayList<>();
        recentlyDeleted = new ArrayList<>();

        mining = IslandMining.getIslandMining();
        farming = IslandFarming.getIslandFarming();
        scheduler.runTaskAsynchronously(randomsky, () -> {
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

        originSelected = colorizeListString(config.getStringList("origins.settings.selected"));
        origin = new UInventory(null, config.getInt("origins.gui.size"), colorize(config.getString("origins.gui.title")));
        final Inventory oi = origin.getInventory();
        final ItemStack b = d(config, "origins.gui.background");
        int origins = 0;
        final Plugin worldEdit = pluginmanager.getPlugin("WorldEdit");
        worldeditF = worldEdit.getDataFolder() + separator + "schematics";

        for(File f : new File(dataFolder + separator + "origins").listFiles()) {
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

        final String dm = config.getString("roles.settings.default member"), dc = config.getString("roles.settings.default creator");
        for(String s : config.getConfigurationSection("roles").getKeys(false)) {
            if(!s.equals("settings")) {
                final String p = "roles." + s + ".";
                final IslandRole role = new IslandRole(s, colorize(config.getString(p + "rank")), colorize(config.getString(p + "name")), colorizeListString(config.getStringList(p + "lore")), config.getStringList(p + "permissions"));
                if(dm.equals(s)) {
                    IslandRole.defaultMember = role;
                } else if(dc.equals(s)) {
                    IslandRole.defaultCreator = role;
                }
            }
        }

        members = new UInventory(null, 54, colorize(config.getString("members.title")));
        membersBack = d(config, "members.back");
        viewingMembers = colorizeListString(config.getStringList("roles.settings.viewing members"));

        for(String s : otherdata.getStringList("recently deleted")) {
            recentlyDeleted.add(toLocation(s));
        }
        sendConsoleMessage("&6[RandomSky] &aLoaded Islands and " + origins + " origins &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void setSpawnLocation(Location location) {
        this.spawn = location;
    }
    public void unload() {
        otherdata.set("spawn", spawn != null ? toString(spawn) : "null");
        final List<String> rd = new ArrayList<>();
        for(Location l : recentlyDeleted) {
            rd.add(toString(l));
        }
        otherdata.set("recently deleted", rd);
        saveOtherData();

        config = null;
        mining = null;
        islandWorld = null;

        RSStorage.unregisterAll(Feature.ISLAND_CHALLENGE, Feature.ISLAND_LEVEL, Feature.ISLAND_ORIGIN, Feature.ISLAND_PROGRESSIVE_SKILL, Feature.ISLAND_RANK, Feature.ISLAND_REGION_PROTECTION, Feature.ISLAND_SKILL, Feature.ISLAND_UPGRADE);
    }
    private void createIslandWorld() {
        Bukkit.createWorld(WorldCreator.name(islandWorld).type(WorldType.FLAT).generatorSettings("3;minecraft:air;127;decoration"));
    }
    private void createIsland(Player player, IslandOrigin origin) {
        final UUID u = player.getUniqueId();
        final Location center = newIslandCenter();
        final Island i = new FileIsland(origin, u, center);
        final RSPlayer pdata = RSPlayer.get(u);
        pdata.setIsland(i);
        player.closeInventory();
        player.sendMessage(ChatColor.YELLOW + "Please wait as your island is being created...");
        try {
            pasteSchematic(origin.schematic, center);
        } catch (Exception e) {
            e.printStackTrace();
        }
        player.teleport(center.clone().add(0.5, 1, 0.5));
        sendStringListMessage(player, config.getStringList("messages.create"), null);
    }
    private void cleanChunk(Chunk chunk) throws IOException {
        final File schematic = new File(worldeditF, "AIR_CHUNK.schematic");
        final Location b = chunk.getBlock(0, 0, 0).getLocation();
        final com.sk89q.worldedit.Vector to = new com.sk89q.worldedit.Vector(b.getBlockX(), b.getBlockY(), b.getBlockZ());
        com.sk89q.worldedit.world.World W = new BukkitWorld(Bukkit.getWorld(islandWorld));
        final WorldData worldData = W.getWorldData();
        final Clipboard clipboard = ClipboardFormat.SCHEMATIC.getReader(new FileInputStream(schematic)).read(worldData);
        final Schematic s = new Schematic(clipboard);
        s.paste(W, to, false, true, null);
    }
    private void pasteSchematic(File schematic, Location l) throws IOException {
        final com.sk89q.worldedit.Vector to = new com.sk89q.worldedit.Vector(l.getBlockX(), l.getBlockY(), l.getBlockZ());
        final com.sk89q.worldedit.world.World W = new BukkitWorld(Bukkit.getWorld(islandWorld));
        final WorldData worldData = W.getWorldData();
        final Clipboard clipboard = ClipboardFormat.SCHEMATIC.getReader(new FileInputStream(schematic)).read(worldData);
        final Schematic s = new Schematic(clipboard);
        s.paste(W, to, false, true, null);
    }

    public void viewHelp(CommandSender sender) {
        if(hasPermission(sender, "RandomSky.island.help", true)) {
            sendStringListMessage(sender, config.getStringList("messages.help"), null);
        }
    }
    public void viewMembers(Player player) {
        if(hasPermission(player, "RandomSky.island.members", true)) {
            final Island is = RSPlayer.get(player.getUniqueId()).getIsland();
            if(is == null) {
                sendStringListMessage(player, config.getStringList("messages.need island"), null);
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
                    item = UMaterial.PLAYER_HEAD_ITEM.getItemStack();
                    final SkullMeta sm = (SkullMeta) item.getItemMeta(); lore.clear();
                    sm.setOwningPlayer(OP);
                    sm.setDisplayName(ChatColor.GREEN + OP.getName());
                    for(String s : viewingMembers) {
                        lore.add(s.replace("{RANK}", R));
                    }
                    sm.setLore(lore); lore.clear();
                    item.setItemMeta(sm);
                    top.setItem(top.firstEmpty(), item);
                }
                player.updateInventory();
            }
        }
    }
    public void tryCreating(Player creator) {
        if(creator != null) {
            final Island is = Island.players.getOrDefault(creator.getUniqueId(), null);
            if(is == null) {
                viewOrigins(creator, true);
            } else {
                sendStringListMessage(creator, config.getStringList("messages.already have an island"), null);
            }
        }
    }
    public void tryInviting(Player sender, OfflinePlayer target) {
        if(hasPermission(sender, "RandomSky.island.invite", true)) {
            final UUID s = sender.getUniqueId();
            final RSPlayer rs = RSPlayer.get(s);
            final Island is = rs.getIsland();
            if(is == null) {
                sendStringListMessage(sender, config.getStringList("messages.need island"), null);
            } else {
                final UUID r = target.getUniqueId();
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{PLAYER}", target.getName());
                replacements.put("{IS_CREATOR}", Bukkit.getOfflinePlayer(is.getCreator()).getName());
                if(s == r) {
                    sendStringListMessage(sender, config.getStringList("messages.cannot invite self"), null);
                } else if(is.members.containsKey(r)) {
                    sendStringListMessage(sender, config.getStringList("messages.invite already member"), replacements);
                } else {
                    final List<RSInvite> invites = is.invites;
                    for(RSInvite i : invites) {
                        if(i.receiver == r) {
                            sendStringListMessage(sender, config.getStringList("messages.invite pending"), replacements);
                            return;
                        }
                    }
                    invites.add(new RSInvite(System.currentTimeMillis(), rs, r, InviteType.ISLAND, 60));
                    sendStringListMessage(sender, config.getStringList("messages.invite sent"), replacements);
                    if(target.isOnline()) {
                        replacements.put("{PLAYER}", sender.getName());
                        sendStringListMessage(target.getPlayer(), config.getStringList("messages.invite receive"), replacements);
                    }
                }
            }
        }
    }
    public void tryJoining(Player player, String target) {
        if(hasPermission(player, "RandomSky.island.join", true) && target != null) {
            final OfflinePlayer creator = Bukkit.getOfflinePlayer(target);
            if(creator != null) {
                final RSPlayer rsp = RSPlayer.get(creator.getUniqueId());
                final Island island = rsp.getIsland();
                final List<String> noinvitefound = config.getStringList("messages.no invite to join");
                final HashMap<String, String> replacements = new HashMap<>();
                if(island == null) {
                    replacements.put("{PLAYER}", creator.getName());
                    sendStringListMessage(player, noinvitefound, replacements);
                } else {
                    final UUID u = player.getUniqueId();
                    final List<RSInvite> invites = island.invites;
                    for(RSInvite i : invites) {
                        if(i.receiver == u) {
                            scheduler.cancelTask(i.expireTask);
                            replacements.put("{PLAYER}", player.getName());
                            replacements.put("{SENDER}", Bukkit.getOfflinePlayer(i.sender.getUUID()).getName());
                            replacements.put("{IS_CREATOR}", Bukkit.getOfflinePlayer(island.getCreator()).getName());
                            final List<String> msg = config.getStringList("messages.invite accept");
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
                sendStringListMessage(player, config.getStringList("messages.unable to warp to island"), replacements);
            } else {
                replacements.put("{PLAYER}", online.getName());
                final Island island = Island.players.getOrDefault(online.getUniqueId(), null), on = Island.valueOf(player.getLocation());
                final Location warp = island != null ? island.getWarp() : null;
                if(island == null || warp == null) {
                    sendStringListMessage(player, config.getStringList("messages.no island to warp to"), replacements);
                } else if(island == on) {
                    sendStringListMessage(player, config.getStringList("messages.warp already on island"), null);
                } else if(online == player) {
                    sendStringListMessage(player, config.getStringList("messages.try warp self"), null);
                } else {
                    player.teleport(warp, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    sendStringListMessage(player, config.getStringList("messages.warp to island"), replacements);
                }
            }
        }
    }
    public void trySettingWarp(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.island.setwarp", true)) {
            final Island is = RSPlayer.get(player.getUniqueId()).getIsland();
            if(is == null) {
                sendStringListMessage(player, config.getStringList("messages.need island"), null);
            } else {
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{PLAYER}", player.getName());
                is.setWarp(player.getLocation());
                sendStringListMessage(player, config.getStringList("messages.setwarp"), replacements);
            }
        }
    }
    public void tryDeletingWarp(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.island.delwarp", true)) {
            final Island is = RSPlayer.get(player.getUniqueId()).getIsland();
            if(is == null) {
                sendStringListMessage(player, config.getStringList("messages.need island"), null);
            } else {
                final Location w = is.getWarp();
                if(w == null) {
                    sendStringListMessage(player, config.getStringList("messages.delwarp no warp"), null);
                } else {
                    is.setWarp(null);
                    sendStringListMessage(player, config.getStringList("messages.delwarp"), null);
                }
            }
        }
    }
    public void tryBanning(@NotNull Player player, @NotNull OfflinePlayer target) {
        if(hasPermission(player, "RandomSky.island.ban", true)) {
            final Island is = hasIsland(player);
            if(is != null) {
                if(player.equals(target.getPlayer())) {
                    sendStringListMessage(player, config.getStringList("messages.cannot ban self"), null);
                } else {
                    final UUID u = target.getUniqueId();
                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("{PLAYER}", player.getName());
                    replacements.put("{TARGET}", target.getName());
                    replacements.put("{IS_CREATOR}", Bukkit.getOfflinePlayer(is.getCreator()).getName());
                    if(!is.getBannedPlayers().contains(u)) {
                        is.ban(target);
                        sendStringListMessage(player, config.getStringList("messages.ban"), replacements);
                    } else {
                        sendStringListMessage(player, config.getStringList("messages.already banned"), replacements);
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
                if(is.getBannedPlayers().contains(u)) {
                    is.banned.remove(u);
                    for(Player p : is.getOnlineMembers()) {
                        sendStringListMessage(p, config.getStringList("messages.unban"), replacements);
                    }
                } else {
                    sendStringListMessage(player, config.getStringList("messages.not banned"), replacements);
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
                    sendStringListMessage(player, config.getStringList("messages.cannot kick self"), null);
                } else if(t == null) {
                    sendStringListMessage(player, config.getStringList("messages.kick not on island"), replacements);
                } else {
                    final Player tar = target.getPlayer();
                    tar.teleport(spawn, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    sendStringListMessage(tar, config.getStringList("messages.been kicked"), replacements);
                    sendStringListMessage(player, config.getStringList("messages.kicked"), replacements);
                    for(Player P : is.getOnlineMembers()) {
                        sendStringListMessage(P, config.getStringList("messages.kicked notify"), replacements);
                    }
                }
            }
        }
    }
    private Island hasIsland(Player player) {
        final Island is = Island.players.getOrDefault(player.getUniqueId(), null);
        if(is == null) {
            sendStringListMessage(player, config.getStringList("messages.need island"), null);
        }
        return is;
    }
    public void tryOpeningToPublic(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.island.open", true)) {
            final Island is = hasIsland(player);
            if(is != null) {
                final boolean isOpen = is.isOpenToPublic();
                if(isOpen) {
                    sendStringListMessage(player, config.getStringList("messages.island already open to public"), null);
                } else {
                    is.setOpenToPublic(true);
                    sendStringListMessage(player, config.getStringList("messages.island open to public"), null);
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
                    sendStringListMessage(player, config.getStringList("messages.your island is not open to the public"), null);
                } else {
                    is.setOpenToPublic(false);
                    sendStringListMessage(player, config.getStringList("messages.no longer open to public"), null);
                }
            }
        }
    }
    private void viewOrigins(Player player, boolean pick) {
        final RSPlayer pdata = RSPlayer.get(player.getUniqueId());
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
                    item = top.getItem(i); itemMeta = item.getItemMeta(); lore.clear();
                    lore.addAll(itemMeta.getLore());
                    lore.addAll(originSelected);
                    itemMeta.setLore(lore); lore.clear();
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
            sendStringListMessage(player, config.getStringList("messages.zero arguments"), null);
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
                    item = top.getItem(i);
                    if(item != null) {
                        item = item.clone(); itemMeta = item.getItemMeta(); lore.clear();
                        if(itemMeta.hasLore()) {
                            for(String s : itemMeta.getLore()) {
                                lore.add(s.replace("{LEVEL}", islandLevel).replace("{ORIGIN}", origin).replace("{RADIUS}", radius).replace("{FARMING_LEVEL}", farmingLevel).replace("{SLAYER_LEVEL}", slayerLevel).replace("{MINING_LEVEL}", miningLevel).replace("{RESOURCE_NODE_VALUE}", rnValue));
                            }
                        }
                        itemMeta.setLore(lore); lore.clear();
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
            p = p + random.nextInt(10);
        }
        return Integer.parseInt(p);
    }
    private Location getRandomIslandLocation(World w) {
        final int x = getRandomLong(1+random.nextInt(7), random.nextInt(2) == 0), z = getRandomLong(1+random.nextInt(7), random.nextInt(2) == 0);
        return new Location(w, x, 100, z);
    }
    private Location newIslandCenter() {
        if(!recentlyDeleted.isEmpty()) {
            final Location l = recentlyDeleted.get(random.nextInt(recentlyDeleted.size()));
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
            final RSPlayer pdata = RSPlayer.get(u);
            final Island is = hasIsland(player);
            if(is == null) return;
            final long l = System.currentTimeMillis(), last = pdata.canDeleteIslandTime;
            if(l < last && !hasPermission(player, "RandomSky.island.delete.bypasstimer", false)) {
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{TIME}", getRemainingTime(last-l));
                sendStringListMessage(player, config.getStringList("messages.delete must wait"), replacements);
            } else {
                player.openInventory(Bukkit.createInventory(player, confirmDelete.getSize(), confirmDelete.getTitle()));
                final Inventory top = player.getOpenInventory().getTopInventory();
                top.setContents(confirmDelete.getInventory().getContents());
                player.updateInventory();
            }
        }
    }
    private void delete(Player player, RSPlayer pdata, Island island) {
        final Location C = island.getCenter();
        final World w = C.getWorld();
        final int r = island.radius, x = C.getBlockX(), z = C.getBlockZ();
        island.delete();
        player.closeInventory();
        player.teleport(spawn);
        pdata.setIsland(null);
        pdata.canDeleteIslandTime = System.currentTimeMillis()+deletionDelay*1000;
        sendStringListMessage(player, config.getStringList("messages.delete"), null);

        scheduler.runTaskAsynchronously(randomsky, () -> {
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
                sendStringListMessage(player, config.getStringList("messages.home doesnt have island"), null);
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
                    is.setLocation("home", l);
                    sendStringListMessage(player, config.getStringList("messages.sethome"), replacements);
                }
            }
        }
    }
    private void dmgDurability(ItemStack is) {
        if(is != null) {
            final String s = is.getType().name();
            if(random.nextInt(100) < 55 && (s.contains("AXE") || s.contains("SWORD") || s.contains("HOE") || s.contains("SPADE") || s.contains("SHOVEL"))) {
                is.setDurability((short) (is.getDurability()+1));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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
                    player.updateInventory();
                    sendStringListMessage(player, config.getStringList("messages.cannot build or break on island"), replacements);
                } else {
                    final ItemStack i = event.getItemInHand();
                    final ResourceNode n = ResourceNode.valueOf(i);
                    final PermissionBlock pb = n == null ? PermissionBlock.valueOf(i) : null;
                    if(n != null) {
                        final IslandLevel requiredLevel = n.getRequiredIslandLevel();
                        final String nodeName = n.getNodeName();
                        if(is.getAllowedNodes().contains(n) || requiredLevel == null || is.getIslandLevel().getLevel() >= requiredLevel.level) {
                            new ActiveResourceNode(n, l);
                            replacements.put("{TYPE}", nodeName);
                            sendStringListMessage(player, mining.config.getStringList("messages.nodes.placed"), replacements);
                            for(Player p : is.getOnlineMembers()) {
                                if(p != player) {
                                    sendStringListMessage(p, mining.config.getStringList("messages.nodes.placed notify"), replacements);
                                }
                            }
                        } else {
                            event.setCancelled(true);
                            replacements.put("{TYPE}", nodeName);
                            replacements.put("{REQUIRED_LEVEL}", Integer.toString(requiredLevel.level));
                            sendStringListMessage(player, mining.config.getStringList("messages.nodes.level too low to place node"), replacements);
                        }
                    } else if(pb != null) {
                        is.getPermissionBlocks().add(new ActivePermissionBlock(l, pb));
                    } else {
                        final IslandPlaceBlockEvent e = new IslandPlaceBlockEvent(player, is, i, b);
                        pluginmanager.callEvent(e);
                        event.setCancelled(e.isCancelled());
                        player.updateInventory();
                    }
                }
            } else {
                event.setCancelled(true);
                sendStringListMessage(player, config.getStringList("messages.need to upgrade island radius to place block there"), null);
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
                    pluginmanager.callEvent(e);
                    final boolean cancelled = e.isCancelled();
                    event.setCancelled(cancelled);
                    if(!cancelled) {
                        final RSPlayer pdata = RSPlayer.get(uuid);
                        boolean did = false, instant = pdata.instantBreakPickup;
                        final MaterialData md = b.getState().getData();
                        if(md instanceof Crops) {
                            final String cs = md.toString();
                            for(ItemStack its : b.getDrops()) {
                                if(instant) giveItem(player, its);
                                else w.dropItemNaturally(l, its);
                            }
                            final UMaterial u = farming.fromBlock(md.toString());
                            int amount = 1;
                            if(u != null) {
                                final ItemStack seed = u.getItemStack();
                                final boolean ripe = cs.contains("RIPE");
                                if(cs.contains("CROPS") || cs.contains("BEETROOT")) {
                                    amount = ripe ? random.nextInt(4) : 1;
                                } else if(cs.contains("POTATO") || cs.contains("CARROT")) {
                                    amount = ripe ? 1+random.nextInt(4) : 1;
                                } else if(cs.contains("NETHER_WARTS")) {
                                    amount = ripe ? 2+random.nextInt(3) : 1;
                                }
                                seed.setAmount(amount);
                                if(instant) giveItem(player, seed);
                                else w.dropItemNaturally(l ,seed);
                            }
                            did = true;
                        }
                        event.setDropItems(false);
                        if(!did) {
                            mining.breakBlock(player, pdata, b);
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
                pluginmanager.callEvent(e);
                event.setCancelled(e.isCancelled());
                if(e.isCancelled()) return;
            } else if(action.equals("PHYSICAL") && is != null) {
                event.setCancelled(true);
            } else return;
        } else return;
        player.updateInventory();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
        final Player player = event.getPlayer();
        final RSPlayer pdata = RSPlayer.get(player.getUniqueId());
        final Block b = event.getBlockClicked();
        final Location L = b.getLocation();
        final BlockFace bf = event.getBlockFace();
        final int x = bf.equals(BlockFace.EAST) ? 1 : bf.equals(BlockFace.WEST) ? -1 : 0, z = bf.equals(BlockFace.SOUTH) ? 1 : bf.equals(BlockFace.NORTH) ? -1 : 0;
        final Location l = b.getWorld().getBlockAt(L.getBlockX()+x, L.getBlockY(), L.getBlockZ()+z).getLocation();
        final Island is = Island.valueOf(l);
        if(is == null) {
            sendStringListMessage(player, config.getStringList("messages.need to upgrade island radius to place block there"), null);
        } else if(is != pdata.getIsland()) {
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{OWNER}", Bukkit.getOfflinePlayer(is.getCreator()).getName());
            sendStringListMessage(player, config.getStringList("messages.cannot build or break on island"), replacements);
        } else return;
        event.setCancelled(true);
        player.updateInventory();
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerBucketFillEvent(PlayerBucketFillEvent event) {
        final Player player = event.getPlayer();
        final RSPlayer pdata = RSPlayer.get(player.getUniqueId());
        final Location l = event.getBlockClicked().getLocation();
        final Island is = Island.valueOf(l);
        if(is == null) {
            sendStringListMessage(player, config.getStringList("messages.need to upgrade island radius to place block there"), null);
        } else if(is != pdata.getIsland()) {
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{OWNER}", Bukkit.getOfflinePlayer(is.getCreator()).getName());
            sendStringListMessage(player, config.getStringList("messages.cannot build or break on island"), replacements);
        } else return;
        event.setCancelled(true);
        player.updateInventory();
    }


    //@EventHandler(priority = EventPriority.LOWEST)
    private void playerChatEvent(AsyncPlayerChatEvent event) {
        if(!event.isCancelled()) {
            final Player player = event.getPlayer();
            final RSPlayer pdata = RSPlayer.get(player.getUniqueId());
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
                final RSPlayer pdata = RSPlayer.get(l.getUniqueId());
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
            final Island is = RSPlayer.get(player.getUniqueId()).getIsland();
            if(is != null) {
                event.setDroppedExp((int) (event.getDroppedExp()*is.XPGainMultiplier));
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
                final Island island = RSPlayer.get(victim.getUniqueId()).getIsland();
                if(c.contains("FIRE") && !firedmg || c.equals("DROWNING") && !drowningdmg || c.equals("LAVA") && !lavadmg || c.equals("FALL") && !falldmg || c.equals("HOT_FLOOR") && !magmaBlockdmg) {
                    event.setCancelled(true);
                } else if(c.equals("VOID")) {
                    event.setCancelled(true);
                    if(island != null) {
                        victim.teleport(island.getHome());
                    } else {
                        victim.teleport(spawn);
                    }
                    sendStringListMessage(victim, config.getStringList("messages.fallen into the void"), null);
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
                            sendStringListMessage(player, config.getStringList("messages.already created origin"), null);
                        }
                    }
                } else if(confirmDelete) {
                    if(current.equals(deleteConfirm)) {
                        final RSPlayer pdata = RSPlayer.get(player.getUniqueId());
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
            is.invites.remove(i);
            if(op.isOnline()) {
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{PLAYER}", Bukkit.getOfflinePlayer(i.receiver).getName());
                sendStringListMessage(op.getPlayer(), config.getStringList("messages.invite expired"), replacements);
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
