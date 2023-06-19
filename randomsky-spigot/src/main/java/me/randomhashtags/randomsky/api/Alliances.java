package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.addon.InviteType;
import me.randomhashtags.randomsky.addon.alliance.Alliance;
import me.randomhashtags.randomsky.addon.alliance.AllianceMember;
import me.randomhashtags.randomsky.addon.alliance.AllianceRelation;
import me.randomhashtags.randomsky.addon.alliance.AllianceRelationship;
import me.randomhashtags.randomsky.addon.file.FileAllianceRelation;
import me.randomhashtags.randomsky.addon.file.FileAllianceRole;
import me.randomhashtags.randomsky.addon.file.FileAllianceUpgrade;
import me.randomhashtags.randomsky.addon.obj.RSInvite;
import me.randomhashtags.randomsky.util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static java.io.File.separator;

public enum Alliances implements RSFeature, CommandExecutor {
    INSTANCE;

    @Override
    public @NotNull RandomSkyFeature get_feature() {
        return RandomSkyFeature.ALLIANCES;
    }

    public YamlConfiguration config;
    private int tagMin, tagMax;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final int l = args.length;
        if(l == 0) {
            viewHelp(sender);
        } else if(l == 1) {
            final String a = args[0];
            if(a.equals("leave")) {
                tryLeaving(player);
            } else if(a.equals("help")) {
                viewHelp(sender);
            } else if(a.equals("disbandall") && hasPermission(sender, "RandomSky.alliance.disbandall", true)) {
                disabandAll(sender, true);
            } else if(player != null) {
                if(a.equals("kick")) {
                    sendStringListMessage(player, getStringList(config, "messages.kick usage"), null);
                } else if(a.equals("join")) {
                    sendStringListMessage(player, getStringList(config, "messages.join usage"), null);
                } else if(a.equals("create")) {
                    sendStringListMessage(player, getStringList(config, "messages.create usage"), null);
                } else if(a.equals("info")) {
                    viewInfo(player, null);
                } else if(!a.equals("member")) {
                    for(AllianceRelation ar : AllianceRelation.paths.values()) {
                        final String p = ar.path;
                        if(a.equals(p)) {
                            final HashMap<String, String> replacements = new HashMap<>();
                            replacements.put("{RELATION}", p);
                            sendStringListMessage(player, getStringList(config, "messages.relation usage"), replacements);
                            return true;
                        }
                    }
                }
            }
        } else {
            final String a = args[0], b = args[1];
            if(a.equals("info")) {
                viewInfo(player, b);
            } else if(a.equals("create")) {
                tryCreating(player, b);
            } else if(a.equals("invite")) {
                tryInviting(player, b);
            } else if(a.equals("join")) {
                tryJoining(player, b);
            } else if(a.equals("kick")) {
                tryKicking(player, b);
            } else if(!a.equals("member")) {
                for(AllianceRelation ar : AllianceRelation.paths.values()) {
                    final String p = ar.path;
                    if(a.equals(p)) {
                        tryChangingRelation(player, b, ar);
                        return true;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        final String folder = DATA_FOLDER + separator + "alliances";
        save(folder, "_settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(folder, "_settings.yml"));

        for(File f : new File(folder + separator + "roles").listFiles()) {
            new FileAllianceRole(f);
        }
        for(File f : new File(folder + separator + "relations").listFiles()) {
            new FileAllianceRelation(f);
        }
        for(File f : new File(folder + separator + "upgrades").listFiles()) {
            new FileAllianceUpgrade(f);
        }

        tagMin = config.getInt("settings.tag min");
        tagMax = config.getInt("settings.tag max");
        sendConsoleMessage("&6[RandomSky] &aLoaded " + RSStorage.getAll(Feature.ALLIANCE_RELATION).size() + " Alliance Relations, " + RSStorage.getAll(Feature.ALLIANCE_ROLE).size() + " Alliance Roles, and " + RSStorage.getAll(Feature.ALLIANCE_UPGRADE).size() + " Alliance Upgrades &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    @Override
    public void unload() {
        RSStorage.unregisterAll(Feature.ALLIANCE_RELATION, Feature.ALLIANCE_ROLE, Feature.ALLIANCE_UPGRADE);
    }

    public void disabandAll(@NotNull CommandSender sender, boolean async) {
        sendStringListMessage(sender, Arrays.asList("&6[RandomSky] &aDisbanding all alliances, please wait..."), null);
        if(async) {
            SCHEDULER.runTaskAsynchronously(RANDOM_SKY, () -> disbandall(sender, true));
        } else {
            disbandall(sender, false);
        }
    }
    private void disbandall(CommandSender sender, boolean async) {
        final long s = System.currentTimeMillis();
        final HashMap<UUID, Alliance> a = Alliance.CACHE;
        final int size = a.size();
        for(Alliance al : a.values()) {
            al.disband();
        }
        sendStringListMessage(sender, Arrays.asList("&6[RandomSky] &aSuccessfully disbanded all (" + size + ") alliances! &e(took " + (System.currentTimeMillis()-s) + "ms)" + (async ? " [async]" : "")), null);
    }

    public void viewHelp(CommandSender sender) {
        if(hasPermission(sender, "RandomSky.alliance.help", true)) {
            for(String s : getStringList(config, "messages.help"))
                sender.sendMessage(center(colorize(s), 60));
        }
    }
    public void tryChangingRelation(Player player, String tag, AllianceRelation relation) {
        if(hasPermission(player, "RandomSky.alliance.relation.change", true)) {
            final Alliance a = FileRSPlayer.get(player.getUniqueId()).getAlliance();
            Alliance target = Alliance.tags.getOrDefault(tag, null);
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{TAG}", target != null ? target.getTag() : tag);
            if(target == null) {
                final OfflinePlayer op = Bukkit.getOfflinePlayer(tag);
                if(op != null) {
                    replacements.put("{PLAYER}", op.getName());
                    final Alliance i = Alliance.players.getOrDefault(op.getUniqueId(), null);
                    if(i == null) {
                        sendStringListMessage(player, getStringList(config, "messages.player belongs to no alliance"), replacements);
                        return;
                    } else {
                        target = i;
                    }
                }
            }

            if(a == null) {
                sendStringListMessage(player, getStringList(config, "messages.must be in an alliance to use command"), replacements);
            } else if(target == null) {
                sendStringListMessage(player, getStringList(config, "messages.unable to find alliance"), replacements);
            } else {
                final UUID t = target.getUUID();
                final HashMap<UUID, AllianceRelationship> r = a.getRelations();
                final AllianceRelation re = r.containsKey(t) ? r.get(t).relation : AllianceRelation.paths.get("neutral");
                replacements.put("{RELATION}", re.getColor() + re.getIdentifier());
                if(a.equals(target)) {
                    sendStringListMessage(player, getStringList(config, "messages.cannot use cmd on self"), replacements);
                } else if(re.equals(relation)) {
                    sendStringListMessage(player, getStringList(config, "messages.already have pending relation"), replacements);
                } else {
                    final AllianceRelationship ship = new AllianceRelationship(System.currentTimeMillis(), relation, true);
                    r.put(t, ship);
                    target.getRelations().put(a.getUUID(), ship);
                    sendStringListMessage(player, getStringList(config, "messages.relation sent"), replacements);
                    final List<String> msg = getStringList(config, "messages.relation received");
                    for(AllianceMember am : target.getOnlineMembers()) {
                        sendStringListMessage(Bukkit.getPlayer(am.getUUID()), msg, replacements);
                    }
                }
            }
        }
    }
    public void viewInfo(CommandSender sender, String tag) {
        if(hasPermission(sender, "RandomSky.alliance.view", true)) {
            final boolean isPlayer = sender instanceof Player;
            final List<String> info = getStringList(config, "messages.info");
            final OfflinePlayer op = tag != null ? Bukkit.getOfflinePlayer(tag) : isPlayer ? (Player) sender : null;
            final UUID opu = op != null ? op.getUniqueId() : null;
            final Alliance a = op != null ? Alliance.players.getOrDefault(opu, null) : null;
            if(tag == null) {
                if(a == null) {
                    sendStringListMessage(sender, getStringList(config, "messages.must be in an alliance to use command"), null);
                } else {
                    sendInfo(sender, a, info);
                }
            } else {
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{TAG}", tag);
                final Alliance al = Alliance.tags.getOrDefault(tag.toLowerCase(), null);
                if(al == null && op == null) {
                    sendStringListMessage(sender, getStringList(config, "messages.unable to find alliance"), replacements);
                } else if(opu != null) {
                    final Alliance t = Alliance.players.getOrDefault(opu, null);
                    if(t != null) {
                        sendInfo(sender, t, info);
                    } else {
                        replacements.put("{INPUT}", tag);
                        sendStringListMessage(sender, getStringList(config, "messages.unable to find online player"), replacements);
                    }
                } else {
                    sendInfo(sender, al, info);
                }
            }
        }
    }
    private void sendInfo(CommandSender sender, Alliance target, List<String> info) {
        String onlineMembers = "", offlineMembers = "";
        final List<UUID> members = target.getMembers();
        final HashMap<UUID, AllianceRelationship> relations = target.getRelations();
        final HashMap<String, String> relation = new HashMap<>();
        final int size = members.size(), S = relations.size();
        for(int i = 0; i < size; i++) {
            final AllianceMember m = members.get(i);
            final OfflinePlayer op = Bukkit.getOfflinePlayer(m.uuid);
            final String c = m.role.color, n = op.getName();
            if(op.isOnline()) {
                onlineMembers = onlineMembers.concat(c + n + (i != size-1 ? ChatColor.RESET + "" + ChatColor.AQUA + ", " : ""));
            } else {
                offlineMembers = offlineMembers.concat(c + n + (i != size-1 ? ChatColor.RESET + "" + ChatColor.AQUA + ", " : ""));
            }
        }
        for(UUID u : relations.keySet()) {
            final Alliance a = Alliance.get(u);
            final AllianceRelation ar = relations.get(u).getRelation();
            final String p = ar.path, t = a.getTag();
            if(!relation.containsKey(p)) {
                relation.put(p, t + ", ");
            } else {
                String o = relation.get(p);
                o = o.concat(t + ", ");
                relation.put(p, o);
            }
        }
        for(AllianceRelation ar : AllianceRelation.paths.values()) {
            final String p = ar.path;
            if(!relation.containsKey(p)) {
                relation.put(p, "");
            }
        }

        final String T = target.getTag();
        for(String s : info) {
            if(!s.contains("{ONLINE_MEMBERS}") && !s.contains("{OFFLINE_MEMBERS}") || s.contains("{ONLINE_MEMBERS}") && !onlineMembers.isEmpty() || s.contains("{OFFLINE_MEMBERS}") && !offlineMembers.isEmpty()) {
                s = s.replace("{TAG}", T).replace("{ONLINE_MEMBERS}", onlineMembers).replace("{OFFLINE_MEMBERS}", offlineMembers);
                for(String k : relation.keySet()) {
                    s = s.replace("{" + k.toUpperCase() + "}", relation.get(k));
                }
                sender.sendMessage(colorize(s));
            }
        }
    }
    public void tryCreating(Player player, String tag) {
        if(hasPermission(player, "RandomSky.alliance.create", true)) {
            final int l = tag.length();
            final UUID u = player.getUniqueId();
            final FileRSPlayer pdata = FileRSPlayer.get(u);
            final Alliance player_alliance = pdata.getAlliance();
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{TAG}", tag);
            if(player_alliance != null) {
                replacements.put("{TAG}", player_alliance.getTag());
                sendStringListMessage(player, getStringList(config, "messages.youre already a member"), replacements);
            } else if(l < tagMin || l > tagMax) {
                sendStringListMessage(player, getStringList(config, "messages.tag needs to be shorter/longer"), replacements);
            } else {
                final Alliance al = Alliance.tags.getOrDefault(tag.toLowerCase(), null);
                if(al != null) {
                    sendStringListMessage(player, getStringList(config, "messages.tag already taken"), replacements);
                } else {
                    final Alliance new_alliance = new Alliance(u, tag);
                    pdata.setAlliance(new_alliance);
                    sendStringListMessage(player, getStringList(config, "messages.create"), replacements);
                }
            }
        }
    }
    public void tryInviting(Player player, String target) {
        if(hasPermission(player, "RandomSky.alliance.invite", true)) {
            final FileRSPlayer pdata = FileRSPlayer.get(player.getUniqueId());
            final Alliance a = pdata.getAlliance();
            if(a == null) {
                sendStringListMessage(player, getStringList(config, "messages.must be in alliance to use command"), null);
            } else {
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{INPUT}", target);
                final OfflinePlayer op = Bukkit.getOfflinePlayer(target);
                if(op == null || !op.isOnline()) {
                    sendStringListMessage(player, getStringList(config, "messages.unable to find online player"), replacements);
                } else if(op.getPlayer() == player) {
                    sendStringListMessage(player, getStringList(config, "messages.cannot use cmd on self"), null);
                } else {
                    replacements.put("{SENDER}", player.getName());
                    replacements.put("{PLAYER}", op.getName());
                    replacements.put("{TAG}", a.getTag());
                    final UUID u = op.getUniqueId();
                    final FileRSPlayer r = FileRSPlayer.get(u);
                    final Alliance al = r.getAlliance();
                    if(al != null) {
                        replacements.put("{TAG}", al.getTag());
                        sendStringListMessage(player, getStringList(config, "messages.target already member"), replacements);
                    } else {
                        final List<String> msg1 = getStringList(config, "messages.target already invited"), msg2 = getStringList(config, "messages.invite sent");
                        final List<RSInvite> invites = a.getInvites();
                        for(RSInvite rsi : invites) {
                            if(rsi.receiver.equals(u)) {
                                sendStringListMessage(player, msg1, replacements);
                                return;
                            }
                        }
                        invites.add(new RSInvite(System.currentTimeMillis(), pdata, u, InviteType.ALLIANCE, 60));

                        for(Player p : a.getOnlineMembers()) {
                            sendStringListMessage(p, msg2, replacements);
                        }
                        sendStringListMessage(op.getPlayer(), getStringList(config, "messages.invite received"), replacements);
                    }
                }
            }
        }
    }
    public void tryJoining(@NotNull Player player, @NotNull String target) {
        if(hasPermission(player, "RandomSky.alliance.join", true)) {
            final UUID player_uuid = player.getUniqueId();
            final FileRSPlayer pdata = FileRSPlayer.get(player_uuid);
            final Alliance player_alliance = pdata.getAlliance();
            final HashMap<String, String> replacements = new HashMap<>();
            if(player_alliance != null) {
                replacements.put("{TAG}", player_alliance.getTag());
                sendStringListMessage(player, getStringList(config, "messages.youre already a member"), replacements);
            } else {
                replacements.put("{TAG}", target);
                final OfflinePlayer op = Bukkit.getOfflinePlayer(target);
                if(op == null) {
                    final Alliance al = Alliance.tags.getOrDefault(target.toLowerCase(), null);
                    if(al == null) {
                        sendStringListMessage(player, getStringList(config, "messages.join no pending invite"), replacements);
                    }
                } else {
                    replacements.put("{TAG}", op.getName());
                    final Alliance alliance = Alliance.players.getOrDefault(op.getUniqueId(), null);
                    if(alliance == null) {
                        sendStringListMessage(player, getStringList(config, "messages.player belongs to no alliance"), replacements);
                    } else {
                        replacements.put("{PLAYER}", player.getName());
                        final List<RSInvite> invites = alliance.getInvites();
                        final List<String> msg = getStringList(config, "messages.joined");
                        for(RSInvite r : invites) {
                            if(r.receiver.equals(player_uuid)) {
                                for(AllianceMember member : alliance.getOnlineMembers()) {
                                    sendStringListMessage(member.getPlayer().getPlayer(), msg, replacements);
                                }
                                alliance.join(player_uuid);
                                r.delete();
                                return;
                            }
                        }
                        sendStringListMessage(player, getStringList(config, "messages.join no pending invite"), replacements);
                    }
                }
            }
        }
    }
    public void tryKicking(@NotNull Player player, String target) {
        if(hasPermission(player, "RandomSky.alliance.kick", true)) {
            final UUID U = player.getUniqueId();
            final FileRSPlayer pdata = FileRSPlayer.get(U);
            final Alliance alliance = pdata.getAlliance();
            if(alliance == null) {
                sendStringListMessage(player, getStringList(config, "messages.must be in an alliance to use command"), null);
            } else {
                final OfflinePlayer target_player = Bukkit.getOfflinePlayer(target);
                final UUID target_player_uuid = target_player.isOnline() ? target_player.getUniqueId() : null;
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{INPUT}", target);
                if(target_player_uuid == null) {
                    sendStringListMessage(player, getStringList(config, "messages.unable to find online player"), replacements);
                } else if(target_player_uuid.equals(U)) {
                    sendStringListMessage(player, getStringList(config, "messages.cannot use cmd on self"), null);
                } else {
                    replacements.put("{PLAYER}", target_player.getName());
                    replacements.put("{KICKER}", player.getName());
                    final boolean online = target_player.isOnline();
                    final FileRSPlayer target_player_data = FileRSPlayer.get(target_player_uuid);
                    if(!online) {
                        target_player_data.load();
                    }
                    final Alliance target_player_alliance = target_player_data.getAlliance();
                    if(target_player_alliance == null || !target_player_alliance.equals(alliance)) {
                        sendStringListMessage(player, getStringList(config, "messages.kick not a member"), replacements);
                    } else {
                        alliance.kick(target_player_uuid);
                        if(online) {
                            sendStringListMessage(target_player.getPlayer(), getStringList(config, "messages.been kicked"), replacements);
                        }
                        sendMsgToMembers(alliance, getStringList(config, "messages.kicked"), replacements);
                    }
                    if(!online) {
                        target_player_data.unload();
                    }
                }
            }
        }
    }
    public void tryLeaving(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.alliance.leave", true)) {
            final UUID player_uuid = player.getUniqueId();
            final Alliance alliance = FileRSPlayer.get(player_uuid).getAlliance();
            if(alliance == null) {
                sendStringListMessage(player, getStringList(config, "messages.must be in an alliance to use command"), null);
            } else {
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{PLAYER}", player.getName());
                alliance.leave(player);
                if(alliance.getMembers().size() == 0) {
                    alliance.disband();
                    sendStringListMessage(player, getStringList(config, "messages.disband"), null);
                } else {
                    sendMsgToMembers(alliance, getStringList(config, "messages.leave notify"), replacements);
                }
            }
        }
    }

    private void sendMsgToMembers(@NotNull Alliance a, @Nullable List<String> msg, @Nullable HashMap<String, String> replacements) {
        for(AllianceMember alliance_member : a.getOnlineMembers()) {
            sendStringListMessage(alliance_member.getPlayer().getPlayer(), msg, replacements);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        final Entity d = event.getDamager(), v = event.getEntity();
        final Player damager = d instanceof Player ? (Player) d : null, victim = v instanceof Player ? (Player) v : null;
        if(damager != null && victim != null) {
            final UUID damager_uuid = damager.getUniqueId(), victim_uuid = victim.getUniqueId();
            final HashMap<UUID, Alliance> players = Alliance.players;
            final Alliance damager_alliance = players.getOrDefault(damager_uuid, null), victim_alliance = players.getOrDefault(victim_uuid, null);
            if(damager_alliance != null && victim_alliance != null) {
                final AllianceRelation alliance_relation = damager_alliance.relation_to(victim_alliance);
                if(!alliance_relation.isDamageable()) {
                    event.setCancelled(true);
                    damager.updateInventory();

                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("{PLAYER}", victim.getName());
                    sendStringListMessage(damager, getStringList(config, "messages.cannot damage due to relation"), replacements);
                }
            }
        }
    }
}
