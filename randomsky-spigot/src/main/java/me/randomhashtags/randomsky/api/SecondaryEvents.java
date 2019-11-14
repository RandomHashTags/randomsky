package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.util.RSFeature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class SecondaryEvents extends RSFeature implements Listener, CommandExecutor {
    private static SecondaryEvents instance;
    public static SecondaryEvents getSecondaryEvents() {
        if(instance == null) instance = new SecondaryEvents();
        return instance;
    }

    public boolean banknoteIsEnabled = false, xpbottleIsEnabled = false;

    private GivedpItem givedp;
    private banknoteevents bn;
    private xpbottleevents xp;
    private YamlConfiguration config;

    private ItemStack banknote, xpbottle;
    private double minBanknote = 0.00;
    private int banknoteValueSlot, xpbottleValueSlot;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) return true;
        final Player player = (Player) sender;
        final String c = cmd.getName();
        final int l = args.length;
        if(c.equals("withdraw") && banknoteIsEnabled) {
            if(l == 0) {
                sendStringListMessage(player, config.getStringList("banknote.messages.usage"), null);
            } else {
                tryWithdrawing(player, args[0]);
            }
        } else if(c.equals("xpbottle") && xpbottleIsEnabled) {
            if(l == 0) {
                sendStringListMessage(player, config.getStringList("xpbottle.messages.usage"), null);
            } else {
                tryWithdrawXP(player, args[0]);
            }
        }
        return true;
    }

    public void load() {
        givedp = GivedpItem.getGivedpItem();
        config = givedp.itemsConfig;
    }
    public void unload() {
    }

    public void enableBanknote() {
        if(banknoteIsEnabled) return;
        banknoteIsEnabled = true;
        minBanknote = config.getDouble("banknote.min");
        banknote = givedp.items.get("banknote").clone();
        final List<String> L = banknote.getItemMeta().getLore();
        for(int i = 0; i < L.size(); i++) {
            if(L.get(i).contains("{VALUE}")) {
                banknoteValueSlot = i;
            }
        }
        bn = new banknoteevents();
        pluginmanager.registerEvents(bn, randomsky);
    }
    public void enableXpbottle() {
        if(xpbottleIsEnabled) return;
        xpbottleIsEnabled = true;
        xpbottle = givedp.items.get("xpbottle").clone();
        final List<String> L = xpbottle.getItemMeta().getLore();
        for(int i = 0; i < L.size(); i++) {
            if(L.get(i).contains("{VALUE}")) {
                xpbottleValueSlot = i;
            }
        }
        xp = new xpbottleevents();
        pluginmanager.registerEvents(xp, randomsky);
    }

    public void disableBanknote() {
        if(!banknoteIsEnabled) return;
        banknoteIsEnabled = false;
        HandlerList.unregisterAll(bn);
        bn = null;
    }
    public void disableXpbottle() {
        if(!xpbottleIsEnabled) return;
        xpbottleIsEnabled = false;
        HandlerList.unregisterAll(xp);
        xp = null;
    }


    public void tryWithdrawing(Player player, String value) {
        if(hasPermission(player, "RandomSky.withdraw", true)) {
            final double v = getRemainingDouble(value), bal = eco.getBalance(player);
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{INPUT}", value);
            replacements.put("{VALUE}", formatDouble(v));
            replacements.put("{BAL}", formatDouble(bal));
            replacements.put("{MIN}", formatDouble(minBanknote));
            if(v <= 0.00) {
                sendStringListMessage(player, config.getStringList("banknote.messages.enter valid"), replacements);
            } else if(v < minBanknote) {
                sendStringListMessage(player, config.getStringList("banknote.messages.need min"), replacements);
            } else if(bal < v) {
                sendStringListMessage(player, config.getStringList("banknote.messages.not enough to withdraw"), replacements);
            } else {
                eco.withdrawPlayer(player, v);
                giveItem(player, givedp.getBanknote(v, player.getName()));
                player.updateInventory();
                sendStringListMessage(player, config.getStringList("banknote.messages.signed"), replacements);
            }
        }
    }
    public void tryWithdrawXP(Player player, String value) {
        if(hasPermission(player, "RandomSky.xpbottle", true)) {
            final int v = getRemainingInt(value), max = getTotalExperience(player);
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{INPUT}", value);
            replacements.put("{XP}", Integer.toString(v));
            if(v < 1) {
                sendStringListMessage(player, config.getStringList("xpbottle.messages.enter valid amount"), replacements);
            } else if(v > max) {
                sendStringListMessage(player, config.getStringList("xpbottle.messages.not enough"), replacements);
            } else {
                setTotalExperience(player, getTotalExperience(player)-v);
                giveItem(player, givedp.getXPBottle(v, player.getName()));
                player.updateInventory();
                sendStringListMessage(player, config.getStringList("xpbottle.messages.withdraw"), replacements);
            }
        }
    }

    private class banknoteevents implements Listener {

        @EventHandler
        private void playerInteractEvent(PlayerInteractEvent event) {
            final Player player = event.getPlayer();
            final ItemStack i = event.getItem();
            if(i != null && event.getAction().name().contains("RIGHT") && i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().hasLore()) {
                final ItemMeta b = banknote.getItemMeta();
                itemMeta = i.getItemMeta();
                final String d = itemMeta.getDisplayName();
                if(d.equals(b.getDisplayName())) {
                    final List<String> L = itemMeta.getLore();
                    if(L.size() > banknoteValueSlot) {
                        final double v = getRemainingDouble(L.get(banknoteValueSlot));
                        if(v >= minBanknote) {
                            event.setCancelled(true);
                            removeItem(player, i, 1);
                            eco.depositPlayer(player, v);
                            final HashMap<String, String> replacements = new HashMap<>();
                            replacements.put("{VALUE}", formatDouble(v));
                            sendStringListMessage(player, config.getStringList("banknote.messages.redeem"), replacements);
                        }
                    }
                }
            }
        }
    }
    private class xpbottleevents implements Listener {

        @EventHandler
        private void playerInteractEvent(PlayerInteractEvent event) {
            final Player player = event.getPlayer();
            final ItemStack i = event.getItem();
            if(i != null && i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().hasLore()) {
                final ItemMeta x = xpbottle.getItemMeta();
                itemMeta = i.getItemMeta();
                final String d = itemMeta.getDisplayName();
                if(d.equals(x.getDisplayName())) {
                    final List<String> L = itemMeta.getLore();
                    if(L.size() > xpbottleValueSlot) {
                        final int v = getRemainingInt(L.get(xpbottleValueSlot));
                        if(v > 0) {
                            player.giveExp(v);
                            event.setCancelled(true);
                            removeItem(player, i, 1);
                            final HashMap<String, String> replacements = new HashMap<>();
                            replacements.put("{XP}", formatInt(v));
                            sendStringListMessage(player, config.getStringList("xpbottle.messages.redeem"), replacements);
                        }
                    }
                }
            }
        }
    }
}
