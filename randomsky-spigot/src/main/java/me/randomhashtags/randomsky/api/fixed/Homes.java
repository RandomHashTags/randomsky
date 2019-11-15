package me.randomhashtags.randomsky.api.fixed;

import me.randomhashtags.randomsky.addon.active.Home;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSPlayer;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class Homes extends RSFeature implements CommandExecutor {
    private static Homes instance;
    public static Homes getHomes() {
        if(instance == null) instance = new Homes();
        return instance;
    }

    public YamlConfiguration config;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) return true;
        final Player player = (Player) sender;
        final String c = cmd.getName();
        final int l = args.length;
        if(c.equals("home")) {
            tryGoing(player, l == 0 ? null : args[0]);
        } else if(c.equals("homelist")) {
            homelist(player);
        } else if(c.equals("sethome")) {
            if(l == 0) {
                sendStringListMessage(player, config.getStringList("messages.sethome usage"), null);
            } else {
                trySetting(player, args[0]);
            }
        } else if(c.equals("delhome")) {
            if(l == 0) {
                sendStringListMessage(player, config.getStringList("messages.delhome usage"), null);
            } else {
                tryDeleting(player, args[0]);
            }
        }
        return true;
    }


    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "homes.yml");
        config = YamlConfiguration.loadConfiguration(new File(dataFolder, "homes.yml"));
        sendConsoleMessage("&6[RandomSky] &aLoaded Homes &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }
    public void tryGoing(Player player, String home) {
        if(hasPermission(player, "RandomSky.home", true)) {
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{NAME}", home);
            final RSPlayer pdata = RSPlayer.get(player.getUniqueId());
            final List<Home> homes = pdata.getHomes();
            if(home == null && homes != null && homes.size() > 0) {
                home = homes.get(0).name;
            } else if(home == null || homes == null || homes.size() == 0) {
                sendStringListMessage(player, config.getStringList("messages.dont have one"), null);
                return;
            }
            for(Home h : homes) {
                if(h.name.equalsIgnoreCase(home)) {
                    replacements.put("{NAME}", h.name);
                    sendStringListMessage(player, config.getStringList("messages.traveling to home"), replacements);
                    player.teleport(h.location, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    return;
                }
            }
            sendStringListMessage(player, config.getStringList("messages.no home of that name"), replacements);
        }
    }
    public void trySetting(Player player, String home) {
        if(hasPermission(player, "RandomSky.sethome", true)) {
            final HashMap<String, String> replacements = new HashMap<>();
            final RSPlayer pdata = RSPlayer.get(player.getUniqueId());
            final List<Home> homes = pdata.getHomes();
            final int maxhomes = getMaxHomes(player);
            replacements.put("{NAME}", home);
            replacements.put("{MAX_HOMES}", Integer.toString(maxhomes));
            if(homes.size()+1 > maxhomes) {
                sendStringListMessage(player, config.getStringList("messages.have max"), replacements);
            } else {
                final Location l = player.getLocation();
                final List<String> msg = config.getStringList("messages.sethome");
                for(Home h : homes) {
                    if(h.name.equalsIgnoreCase(home)) {
                        h.location = l;
                        sendStringListMessage(player, msg, replacements);
                        return;
                    }
                }
                homes.add(new Home(home, l));
                sendStringListMessage(player, msg, replacements);
            }
        }
    }
    public int getMaxHomes(Player player) {
        for(int i = 100; i >= 0; i--) {
            if(player.hasPermission("RandomSky.sethome." + i)) {
                return i;
            }
        }
        return 0;
    }
    public void tryDeleting(Player player, String home) {
        if(hasPermission(player, "RandomSky.delhome", true)) {
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{NAME}", home);
            final RSPlayer pdata = RSPlayer.get(player.getUniqueId());
            final List<Home> homes = pdata.getHomes();
            for(Home h : homes) {
                if(h.name.equalsIgnoreCase(home)) {
                    homes.remove(h);
                    sendStringListMessage(player, config.getStringList("messages.delhome"), replacements);
                    return;
                }
            }
            sendStringListMessage(player, config.getStringList("messages.delhome not found"), replacements);
        }
    }
    public void homelist(Player player) {
        if(hasPermission(player, "RandomSky.homelist", true)) {
            final RSPlayer pdata = RSPlayer.get(player.getUniqueId());
            final List<Home> homes = pdata.getHomes();
            final String homesize = Integer.toString(homes.size());
            for(String s : config.getStringList("messages.homelist")) {
                if(s.equals("{HOMES}")) {
                    final List<String> p = config.getStringList("messages.home in list");
                    for(Home h : homes) {
                        final String n = h.name;
                        final Location l = h.location;
                        final String x = Integer.toString(l.getBlockX()), y = Integer.toString(l.getBlockY()), z = Integer.toString(l.getBlockZ());
                        for(String o : p) {
                            player.sendMessage(colorize(o.replace("{NAME}", n).replace("{X}", x).replace("{Y}", y).replace("{Z}", z)));
                        }
                    }
                } else {
                    player.sendMessage(colorize(s.replace("{HOME_SIZE}", homesize)));
                }
            }
        }
    }
}
