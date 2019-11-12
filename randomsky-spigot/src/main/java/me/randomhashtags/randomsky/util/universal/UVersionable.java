package me.randomhashtags.randomsky.util.universal;

import me.randomhashtags.randomsky.RandomSky;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;

import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public interface UVersionable {
    BukkitScheduler scheduler = Bukkit.getScheduler();
    ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
    PluginManager pluginmanager = Bukkit.getPluginManager();
    ConsoleCommandSender console = Bukkit.getConsoleSender();

    RandomSky randomsky = RandomSky.getPlugin;
    File dataFolder = randomsky.getDataFolder();
    Random random = new Random();


    default ItemStack getClone(ItemStack is) {
        return getClone(is, null);
    }
    default ItemStack getClone(ItemStack is, ItemStack def) {
        return is != null ? is.clone() : def;
    }
    default Color getColor(final String path) {
        if(path == null) {
            return null;
        } else {
            switch (path.toLowerCase()) {
                case "aqua": return Color.AQUA;
                case "black": return Color.BLACK;
                case "blue": return Color.BLUE;
                case "fuchsia": return Color.FUCHSIA;
                case "gray": return Color.GRAY;
                case "green": return Color.GREEN;
                case "lime": return Color.LIME;
                case "maroon": return Color.MAROON;
                case "navy": return Color.NAVY;
                case "olive": return Color.OLIVE;
                case "orange": return Color.ORANGE;
                case "purple": return Color.PURPLE;
                case "red": return Color.RED;
                case "silver": return Color.SILVER;
                case "teal": return Color.TEAL;
                case "white": return Color.WHITE;
                case "yellow": return Color.YELLOW;
                default: return null;
            }
        }
    }
    default PotionEffectType getPotionEffectType(String input) {
        if(input != null && !input.isEmpty()) {
            switch (input.toUpperCase()) {
                case "STRENGTH": return PotionEffectType.INCREASE_DAMAGE;
                case "MINING_FATIGUE": return PotionEffectType.SLOW_DIGGING;
                case "SLOWNESS": return PotionEffectType.SLOW;
                case "HASTE": return PotionEffectType.FAST_DIGGING;
                case "JUMP": return PotionEffectType.JUMP;
                case "INSTANT_HEAL":
                case "INSTANT_HEALTH": return PotionEffectType.HEAL;
                case "INSTANT_HARM":
                case "INSTANT_DAMAGE": return PotionEffectType.HARM;
                default:
                    for(PotionEffectType p : PotionEffectType.values()) {
                        if(p != null && input.equalsIgnoreCase(p.getName())) {
                            return p;
                        }
                    }
                    return null;
            }
        } else {
            return null;
        }
    }
    default void sendConsoleMessage(String msg) {
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    default String formatDouble(double d) {
        String decimals = Double.toString(d).split("\\.")[1];
        if(decimals.equals("0")) { decimals = ""; } else { decimals = "." + decimals; }
        return formatInt((int) d) + decimals;
    }
    default String formatLong(long l) {
        final String f = Long.toString(l);
        final boolean c = f.contains(".");
        String decimals = c ? f.split("\\.")[1] : f;
        decimals = c ? decimals.equals("0") ? "" : "." + decimals : "";
        return formatInt((int) l) + decimals;
    }
    default String formatBigDecimal(BigDecimal b) { return formatBigDecimal(b, false); }
    default String formatBigDecimal(BigDecimal b, boolean currency) { return (currency ? NumberFormat.getCurrencyInstance() : NumberFormat.getInstance()).format(b); }
    default String formatInt(int integer) { return String.format("%,d", integer); }

    default int getRemainingInt(String string) {
        string = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', string)).replaceAll("\\p{L}", "").replaceAll("\\s", "").replaceAll("\\p{P}", "").replaceAll("\\p{S}", "");
        return string.isEmpty() ? -1 : Integer.parseInt(string);
    }
    default Double getRemainingDouble(String string) {
        string = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', string).replaceAll("\\p{L}", "").replaceAll("\\p{Z}", "").replaceAll("\\.", "d").replaceAll("\\p{P}", "").replaceAll("\\p{S}", "").replace("d", "."));
        return string.isEmpty() ? -1.00 : Double.parseDouble(string.contains(".") && string.split("\\.").length > 1 && string.split("\\.")[1].length() > 2 ? string.substring(0, string.split("\\.")[0].length() + 3) : string);
    }

    default List<String> colorizeListString(List<String> input) {
        final List<String> i = new ArrayList<>();
        for(String s : input) {
            i.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return i;
    }
    default String toReadableDate(Date d, String format) { return new SimpleDateFormat(format).format(d); }

    default double levelToExp(int level) { return level <= 16 ? (level * level) + (level * 6) : level <= 31 ? (2.5 * level * level) - (40.5 * level) + 360 : (4.5 * level * level) - (162.5 * level) + 2220; }
    default int getTotalExperience(Player player) {
        final double levelxp = levelToExp(player.getLevel()), nextlevelxp = levelToExp(player.getLevel() + 1), difference = nextlevelxp - levelxp;
        final double p = (levelxp + (difference * player.getExp()));
        return (int) Math.round(p);
    }
    default void setTotalExperience(Player player, int total) {
        player.setTotalExperience(0);
        player.setExp(0f);
        player.setLevel(0);
        player.giveExp(total);
    }
}
