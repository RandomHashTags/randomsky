package me.randomhashtags.randomsky.api;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randomsky.addon.alliance.Alliance;
import me.randomhashtags.randomsky.addon.alliance.AllianceMember;
import me.randomhashtags.randomsky.addon.alliance.AllianceRelation;
import me.randomhashtags.randomsky.addon.alliance.AllianceRelationship;
import me.randomhashtags.randomsky.addon.file.FileAllianceRelation;
import me.randomhashtags.randomsky.addon.file.FileAllianceRole;
import me.randomhashtags.randomsky.addon.file.FileAllianceUpgrade;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSPlayer;
import me.randomhashtags.randomsky.util.RSStorage;
import me.randomhashtags.randomsky.util.enums.InviteType;
import me.randomhashtags.randomsky.util.classes.RSInvite;
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

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static java.io.File.separator;

public class Alliances extends RSFeature implements CommandExecutor {
    private static Alliances instance;
    public static Alliances getAlliances() {
        if(instance == null) instance = new Alliances();
        return instance;
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
                    sendStringListMessage(player, config.getStringList("messages.kick usage"), null);
                } else if(a.equals("join")) {
                    sendStringListMessage(player, config.getStringList("messages.join usage"), null);
                } else if(a.equals("create")) {
                    sendStringListMessage(player, config.getStringList("messages.create usage"), null);
                } else if(a.equals("info")) {
                    viewInfo(player, null);
                } else if(!a.equals("member")) {
                    for(AllianceRelation ar : AllianceRelation.paths.values()) {
                        final String p = ar.path;
                        if(a.equals(p)) {
                            final HashMap<String, String> replacements = new HashMap<>();
                            replacements.put("{RELATION}", p);
                            sendStringListMessage(player, config.getStringList("messages.relation usage"), replacements);
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

    public void load() {
        final long started = System.currentTimeMillis();
        final String folder = dataFolder + separator + "alliances";
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
    public void unload() {
        RSStorage.unregisterAll(Feature.ALLIANCE_RELATION, Feature.ALLIANCE_ROLE, Feature.ALLIANCE_UPGRADE);
    }

    public void disabandAll(CommandSender sender, boolean async) {
        sendStringListMessage(sender, Arrays.asList("&6[RandomSky] &aDisbanding all alliances, please wait..."), null);
        if(async) {
            scheduler.runTaskAsynchronously(randomsky, () -> disbandall(sender, true));
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
            for(String s : config.getStringList("messages.help"))
                sender.sendMessage(center(colorize(s), 60));
        }
    }
    public void tryChangingRelation(Player player, String tag, AllianceRelation relation) {
        if(hasPermission(player, "RandomSky.alliance.relation.change", true)) {
            final Alliance a = RSPlayer.get(player.getUniqueId()).getAlliance();
            Alliance target = Alliance.tags.getOrDefault(tag, null);
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{TAG}", target != null ? target.getTag() : tag);
            if(target == null) {
                final OfflinePlayer op = Bukkit.getOfflinePlayer(tag);
                if(op != null) {
                    replacements.put("{PLAYER}", op.getName());
                    final Alliance i = Alliance.players.getOrDefault(op.getUniqueId(), null);
                    if(i == null) {
                        sendStringListMessage(player, config.getStringList("messages.player belongs to no alliance"), replacements);
                        return;
                    } else {
                        target = i;
                    }
                }
            }

            if(a == null) {
                sendStringListMessage(player, config.getStringList("messages.must be in an alliance to use command"), replacements);
            } else if(target == null) {
                sendStringListMessage(player, config.getStringList("messages.unable to find alliance"), replacements);
            } else {
                final UUID t = target.getUUID();
                final HashMap<UUID, AllianceRelationship> r = a.getRelations();
                final AllianceRelation re = r.containsKey(t) ? r.get(t).relation : AllianceRelation.paths.get("neutral");
                replacements.put("{RELATION}", re.getColor() + re.getIdentifier());
                if(a.equals(target)) {
                    sendStringListMessage(player, config.getStringList("messages.cannot use cmd on self"), replacements);
                } else if(re.equals(relation)) {
                    sendStringListMessage(player, config.getStringList("messages.already have pending relation"), replacements);
                } else {
                    final AllianceRelationship ship = new AllianceRelationship(System.currentTimeMillis(), relation, true);
                    r.put(t, ship);
                    target.getRelations().put(a.getUUID(), ship);
                    sendStringListMessage(player, config.getStringList("messages.relation sent"), replacements);
                    final List<String> msg = config.getStringList("messages.relation received");
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
            final List<String> info = config.getStringList("messages.info");
            final OfflinePlayer op = tag != null ? Bukkit.getOfflinePlayer(tag) : isPlayer ? (Player) sender : null;
            final UUID opu = op != null ? op.getUniqueId() : null;
            final Alliance a = op != null ? Alliance.players.getOrDefault(opu, null) : null;
            if(tag == null) {
                if(a == null) {
                    sendStringListMessage(sender, config.getStringList("messages.must be in an alliance to use command"), null);
                } else {
                    sendInfo(sender, a, info);
                }
            } else {
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{TAG}", tag);
                final Alliance al = Alliance.tags.getOrDefault(tag.toLowerCase(), null);
                if(al == null && op == null) {
                    sendStringListMessage(sender, config.getStringList("messages.unable to find alliance"), replacements);
                } else if(opu != null) {
                    final Alliance t = Alliance.players.getOrDefault(opu, null);
                    if(t != null) {
                        sendInfo(sender, t, info);
                    } else {
                        replacements.put("{INPUT}", tag);
                        sendStringListMessage(sender, config.getStringList("messages.unable to find online player"), replacements);
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
            final RSPlayer p = RSPlayer.get(u);
            final Alliance a = p.getAlliance();
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{TAG}", tag);
            if(a != null) {
                replacements.put("{TAG}", a.getTag());
                sendStringListMessage(player, config.getStringList("messages.youre already a member"), replacements);
            } else if(l < tagMin || l > tagMax) {
                sendStringListMessage(player, config.getStringList("messages.tag needs to be shorter/longer"), replacements);
            } else {
                final Alliance al = Alliance.tags.getOrDefault(tag.toLowerCase(), null);
                if(al != null) {
                    sendStringListMessage(player, config.getStringList("messages.tag already taken"), replacements);
                } else {
                    final Alliance ali = new Alliance(u, tag);
                    p.setAlliance(ali);
                    sendStringListMessage(player, config.getStringList("messages.create"), replacements);
                }
            }
        }
    }
    public void tryInviting(Player player, String target) {
        if(hasPermission(player, "RandomSky.alliance.invite", true)) {
            final RSPlayer pdata = RSPlayer.get(player.getUniqueId());
            final Alliance a = pdata.getAlliance();
            if(a == null) {
                sendStringListMessage(player, config.getStringList("messages.must be in alliance to use command"), null);
            } else {
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{INPUT}", target);
                final OfflinePlayer op = Bukkit.getOfflinePlayer(target);
                if(op == null || !op.isOnline()) {
                    sendStringListMessage(player, config.getStringList("messages.unable to find online player"), replacements);
                } else if(op.getPlayer() == player) {
                    sendStringListMessage(player, config.getStringList("messages.cannot use cmd on self"), null);
                } else {
                    replacements.put("{SENDER}", player.getName());
                    replacements.put("{PLAYER}", op.getName());
                    replacements.put("{TAG}", a.getTag());
                    final UUID u = op.getUniqueId();
                    final RSPlayer r = RSPlayer.get(u);
                    final Alliance al = r.getAlliance();
                    if(al != null) {
                        replacements.put("{TAG}", al.getTag());
                        sendStringListMessage(player, config.getStringList("messages.target already member"), replacements);
                    } else {
                        final List<String> msg1 = config.getStringList("messages.target already invited"), msg2 = config.getStringList("messages.invite sent");
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
                        sendStringListMessage(op.getPlayer(), config.getStringList("messages.invite received"), replacements);
                    }
                }
            }
        }
    }
    public void tryJoining(Player player, String target) {
        if(hasPermission(player, "RandomSky.alliance.join", true)) {
            final UUID U = player.getUniqueId();
            final RSPlayer pdata = RSPlayer.get(U);
            final Alliance a = pdata.getAlliance();
            final HashMap<String, String> replacements = new HashMap<>();
            if(a != null) {
                replacements.put("{TAG}", a.getTag());
                sendStringListMessage(player, config.getStringList("messages.youre already a member"), replacements);
            } else {
                replacements.put("{TAG}", target);
                final OfflinePlayer op = Bukkit.getOfflinePlayer(target);
                if(op == null) {
                    final Alliance al = Alliance.tags.getOrDefault(target.toLowerCase(), null);
                    if(al == null) {
                        sendStringListMessage(player, config.getStringList("messages.join no pending invite"), replacements);
                    }
                } else {
                    replacements.put("{TAG}", op.getName());
                    final Alliance al = Alliance.players.getOrDefault(op.getUniqueId(), null);
                    if(al == null) {
                        sendStringListMessage(player, config.getStringList("messages.player belongs to no alliance"), replacements);
                    } else {
                        replacements.put("{PLAYER}", player.getName());
                        final List<RSInvite> invites = al.getInvites();
                        final List<String> msg = config.getStringList("messages.joined");
                        for(RSInvite r : invites) {
                            if(r.receiver.equals(U)) {
                                for(Player p : al.getOnlineMembers()) {
                                    sendStringListMessage(p, msg, replacements);
                                }
                                al.join(player);
                                r.delete();
                                return;
                            }
                        }
                        sendStringListMessage(player, config.getStringList("messages.join no pending invite"), replacements);
                    }
                }
            }
        }
    }
    public void tryKicking(@NotNull Player player, String target) {
        if(hasPermission(player, "RandomSky.alliance.kick", true)) {
            final UUID U = player.getUniqueId();
            final RSPlayer pdata = RSPlayer.get(U);
            final Alliance a = pdata.getAlliance();
            if(a == null) {
                sendStringListMessage(player, config.getStringList("messages.must be in an alliance to use command"), null);
            } else {
                final OfflinePlayer op = Bukkit.getOfflinePlayer(target);
                final UUID u = op.isOnline() ? op.getUniqueId() : null;
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{INPUT}", target);
                if(u == null) {
                    sendStringListMessage(player, config.getStringList("messages.unable to find online player"), replacements);
                } else if(u.equals(U)) {
                    sendStringListMessage(player, config.getStringList("messages.cannot use cmd on self"), null);
                } else {
                    replacements.put("{PLAYER}", op.getName());
                    replacements.put("{KICKER}", player.getName());
                    final boolean online = op.isOnline();
                    final RSPlayer t = RSPlayer.get(u);
                    if(!online) t.load();
                    final Alliance A = t.getAlliance();
                    if(A == null || !A.equals(a)) {
                        sendStringListMessage(player, config.getStringList("messages.kick not a member"), replacements);
                    } else {
                        a.kick(op);
                        if(online) sendStringListMessage(op.getPlayer(), config.getStringList("messages.been kicked"), replacements);
                        sendMsgToMembers(a, config.getStringList("messages.kicked"), replacements);
                    }
                    if(!online) t.unload();
                }
            }
        }
    }
    public void tryLeaving(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.alliance.leave", true)) {
            final UUID u = player.getUniqueId();
            final Alliance a = RSPlayer.get(u).getAlliance();
            if(a == null) {
                sendStringListMessage(player, config.getStringList("messages.must be in an alliance to use command"), null);
            } else {
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{PLAYER}", player.getName());
                a.leave(player);
                if(a.getMembers().size() == 0) {
                    a.disband();
                    sendStringListMessage(player, config.getStringList("messages.disband"), null);
                } else {
                    sendMsgToMembers(a, config.getStringList("messages.leave notify"), replacements);
                }
            }
        }
    }

    private void sendMsgToMembers(Alliance a, List<String> msg, HashMap<String, String> replacements) {
        for(AllianceMember m : a.getOnlineMembers()) {
            sendStringListMessage(m.getPlayer().getPlayer(), msg, replacements);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        final Entity d = event.getDamager(), v = event.getEntity();
        final Player damager = d instanceof Player ? (Player) d : null, victim = v instanceof Player ? (Player) v : null;
        if(damager != null && victim != null) {
            final UUID da = damager.getUniqueId(), vi = victim.getUniqueId();
            final HashMap<UUID, Alliance> players = Alliance.players;
            final Alliance a = players.getOrDefault(da, null), b = players.getOrDefault(vi, null);
            if(a != null && b != null) {
                final AllianceRelation r = a.relationTo(b);
                if(!r.isDamageable()) {
                    event.setCancelled(true);
                    damager.updateInventory();

                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("{PLAYER}", victim.getName());
                    sendStringListMessage(damager, config.getStringList("messages.cannot damage due to relation"), replacements);
                }
            }
        }
    }
}
