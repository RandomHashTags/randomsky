package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.addon.obj.JackpotStats;
import me.randomhashtags.randomsky.event.JackpotPurchaseTicketsEvent;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.FileRSPlayer;
import me.randomhashtags.randomsky.universal.UInventory;
import me.randomhashtags.randomsky.util.RandomSkyFeature;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

public enum Jackpot implements RSFeature, CommandExecutor {
    INSTANCE;

    public YamlConfiguration config;
    public int task;
    public List<Integer> countdownTasks;

    private UInventory gui;
    private List<Integer> confirmSlots, cancelSlots;
    public BigDecimal tax, ticketCost, maxTickets, minTickets, playersPerPage, value;
    public long winnerPickedEvery, pickNextWinner;
    public HashMap<UUID, BigDecimal> ticketsSold, top, purchasing;

    public String getIdentifier() { return "JACKPOT"; }

    @Override
    public @NotNull RandomSkyFeature get_feature() {
        return RandomSkyFeature.JACKPOT;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final int l = args.length;
        if(l == 0) {
            view(sender);
        } else {
            final String a = args[0];
            switch (a) {
                case "pickwinner":
                    if(hasPermission(sender, "RandomSky.jackpot.pickwinner", true)) {
                        pickWinner();
                    }
                    break;
                case "stats":
                    if(player != null) {
                        viewStats(player);
                    }
                    break;
                case "top":
                    viewTop(sender, l == 1 ? 1 : getRemainingInt(args[1]));
                    break;
                case "buy":
                    final List<String> b = config.getStringList("messages.enter valid ticket amount");
                    if(l == 1) {
                        sendStringListMessage(player, b, null);
                    } else {
                        final BigDecimal amount = BigDecimal.valueOf(getRemainingDouble(args[1]));
                        final int amt = amount.intValue();
                        if(amt < minTickets.intValue() || amt > maxTickets.intValue()) {
                            sendStringListMessage(player, b, null);
                        } else {
                            confirmPurchaseTickets(player, amount);
                        }
                    }
                    break;
                case "toggle":
                case "stfu":
                    if(player != null) {
                        tryToggleNotifications(player);
                    }
                    break;
                default:
                    viewHelp(sender);
                    break;
            }
        }
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "jackpot.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "jackpot.yml"));

        gui = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        final Inventory gi = gui.getInventory();
        final ItemStack confirm = d(config, "gui.confirm"), cancel = d(config, "gui.cancel");
        confirmSlots = new ArrayList<>();
        cancelSlots = new ArrayList<>();
        for(String s : config.getConfigurationSection("gui").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("confirm") && !s.equals("cancel")) {
                final int slot = config.getInt("gui." + s + ".slot");
                final String i = config.getString("gui." + s + ".item").toLowerCase();
                final boolean co = i.equals("confirm"), ca = i.equals("cancel");
                if(co) {
                    confirmSlots.add(slot);
                } else if(ca) {
                    cancelSlots.add(slot);
                }
                final ItemStack item = co ? confirm : ca ? cancel : d(config, "gui." + s);
                gi.setItem(slot, item);
            }
        }

        tax = BigDecimal.valueOf(config.getDouble("settings.tax"));
        ticketCost = BigDecimal.valueOf(config.getInt("settings.ticket cost"));
        maxTickets = BigDecimal.valueOf(config.getInt("settings.max tickets"));
        minTickets = BigDecimal.valueOf(config.getInt("settings.min tickets"));
        playersPerPage = BigDecimal.valueOf(config.getInt("messages.players per page"));
        winnerPickedEvery = config.getInt("settings.winner picked every");
        ticketsSold = new HashMap<>();
        top = new HashMap<>();
        purchasing = new HashMap<>();

        final YamlConfiguration a = otherdata;
        final long e = a.getLong("jackpot.pick next winner");
        pickNextWinner = e == 0 ? started+winnerPickedEvery*1000 : e;

        value = BigDecimal.valueOf(a.getDouble("jackpot.value"));
        final ConfigurationSection j = a.getConfigurationSection("jackpot");
        if(j != null) {
            for(String s : j.getKeys(false)) {
                if(!s.equals("value") && !s.equals("pick next winner")) {
                    final UUID u = UUID.fromString(s);
                    final BigDecimal b = BigDecimal.valueOf(a.getInt("jackpot." + s));
                    ticketsSold.put(u, b);
                }
            }
        }
        startTask(started);
        sendConsoleMessage("&6[RandomPackage] &aLoaded Jackpot &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        otherdata.set("jackpot", null);
        otherdata.set("jackpot.pick next winner", pickNextWinner);
        otherdata.set("jackpot.value", value);
        for(UUID u : ticketsSold.keySet()) {
            otherdata.set("jackpot." + u.toString(), ticketsSold.get(u).intValue());
        }
        saveOtherData();
        SCHEDULER.cancelTask(task);
        for(int i : countdownTasks) {
            SCHEDULER.cancelTask(i);
        }
    }

    public void pickWinner() {
        final List<UUID> tic = getTickets();
        final int size = tic.size();
        if(size > 0) {
            final UUID winner_uuid = tic.get(RANDOM.nextInt(size));
            final OfflinePlayer winner_offline_player = Bukkit.getOfflinePlayer(winner_uuid);
            final HashMap<String, String> replacements = new HashMap<>();
            final BigDecimal t = ticketsSold.get(winner_uuid);
            final BigDecimal taxed = value.multiply(tax), total = value.subtract(taxed);
            ECONOMY.depositPlayer(winner_offline_player, total.doubleValue());

            final FileRSPlayer pdata = FileRSPlayer.get(winner_uuid);
            final JackpotStats jackpot_stats = pdata.getJackpotStats();
            jackpot_stats.setTimesWon(jackpot_stats.getTimesWon().add(BigDecimal.ONE));
            jackpot_stats.setWonCash(jackpot_stats.getWonCash().add(total));
            if(!pdata.isOnline()) {
                pdata.unload();
            }

            final String percent = formatDouble(getPercent(t, size)), tt = formatInt(size);

            replacements.put("{PLAYER}", winner_offline_player.getName());
            replacements.put("{TICKETS}", formatBigDecimal(t));
            replacements.put("{TICKETS%}", percent);
            replacements.put("{TOTAL_TICKETS}", tt);
            replacements.put("{$}", formatBigDecimal(total));

            final Collection<? extends Player> o = Bukkit.getOnlinePlayers();
            for(String s : config.getStringList("messages.won")) {
                for(String r : replacements.keySet()) s = s.replace(r, replacements.get(r));
                s = colorize(s);
                for(Player p : o) {
                    p.sendMessage(s);
                }
            }
            value = BigDecimal.ZERO;
            ticketsSold.clear();
        }
        final long time = System.currentTimeMillis();
        pickNextWinner = time+winnerPickedEvery*1000;
        startTask(time);
    }
    public List<UUID> getTickets() {
        final List<UUID> a = new ArrayList<>();
        for(UUID u : ticketsSold.keySet()) {
            for(int i = 1; i <= ticketsSold.get(u).intValue(); i++) {
                a.add(u);
            }
        }
        return a;
    }
    public double getPercent(BigDecimal tickets, int size) {
        return round(tickets.divide(BigDecimal.valueOf((double) (size == 0 ? 1 : size))).multiply(BigDecimal.valueOf(100)).doubleValue(), 2);
    }
    private void broadcastCountdown(long timeleft) {
        final List<String> c = config.getStringList("messages.countdown");
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put("{TIME}", getRemainingTime(timeleft));
        replacements.put("{$}", formatBigDecimal(value));
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(FileRSPlayer.get(p.getUniqueId()).getJackpotStats().receivesNotifications()) {
                sendStringListMessage(p, c, replacements);
            }
        }
    }
    private void startTask(long time) {
        final long pick = ((pickNextWinner-time)/1000)*20;
        if(countdownTasks != null) {
            for(int i : countdownTasks) {
                SCHEDULER.cancelTask(i);
            }
        }
        task = SCHEDULER.scheduleSyncDelayedTask(RANDOM_SKY, this::pickWinner, pick);
        countdownTasks = new ArrayList<>();
        for(String s : config.getStringList("messages.countdowns")) {
            final long delay = (getDelay(s)/1000)*20;
            countdownTasks.add(SCHEDULER.scheduleSyncDelayedTask(RANDOM_SKY, () -> {
                broadcastCountdown((delay/20)*1000);
            }, pick-delay));
        }
    }
    public void confirmPurchaseTickets(@NotNull Player player, @NotNull BigDecimal tickets) {
        if(hasPermission(player, "RandomSky.jackpot.buy", true)) {
            player.closeInventory();
            purchasing.put(player.getUniqueId(), tickets);
            final String a = formatBigDecimal(tickets), p = formatBigDecimal(ticketCost.multiply(tickets));
            final int s = gui.getSize();
            player.openInventory(Bukkit.createInventory(player, s, gui.getTitle().replace("{AMOUNT}", a)));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());
            for(int i = 0; i < s; i++) {
                final ItemStack item = top.getItem(i);
                if(item != null) {
                    final ItemMeta itemMeta = item.getItemMeta();
                    final List<String> lore = new ArrayList<>();
                    if(itemMeta.hasLore()) {
                        for(String l : itemMeta.getLore()) {
                            lore.add(l.replace("{AMOUNT}", a).replace("{$}", p));
                        }
                    }
                    itemMeta.setLore(lore);
                    item.setItemMeta(itemMeta);
                }
            }
            player.updateInventory();
        }
    }
    public void purchaseTickets(@NotNull Player player, @NotNull BigDecimal tickets) {
        final BigDecimal cost = tickets.multiply(ticketCost);
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put("{AMOUNT}", formatBigDecimal(tickets));
        replacements.put("{$}", formatBigDecimal(cost));
        if(ECONOMY.withdrawPlayer(player, cost.doubleValue()).transactionSuccess()) {
            final JackpotPurchaseTicketsEvent e = new JackpotPurchaseTicketsEvent(player, tickets, cost);
            PLUGIN_MANAGER.callEvent(e);
            if(!e.isCancelled()) {
                final UUID u = player.getUniqueId();
                final JackpotStats pdata = FileRSPlayer.get(u).getJackpotStats();
                pdata.setTicketsPurchased(pdata.getTicketsPurchased().add(tickets));
                ticketsSold.put(u, ticketsSold.getOrDefault(u, BigDecimal.ZERO).add(tickets));
                value = value.add(cost);
                sendStringListMessage(player, config.getStringList("messages.purchased"), replacements);
            }
        } else {
            sendStringListMessage(player, config.getStringList("messages.cannot afford"), replacements);
        }
    }

    public void view(@NotNull CommandSender sender) {
        if(hasPermission(sender, "RandomSky.jackpot", true)) {
            final String time = getRemainingTime(pickNextWinner-System.currentTimeMillis());
            final HashMap<String, String> replacements = new HashMap<>();
            final BigDecimal t = sender instanceof Player ? ticketsSold.getOrDefault(((Player) sender).getUniqueId(), BigDecimal.ZERO) : BigDecimal.ZERO;
            final int s = getTickets().size();
            replacements.put("{VALUE}", formatBigDecimal(value));
            replacements.put("{TICKETS}", formatDouble(getTickets().size()));
            replacements.put("{YOUR_TICKETS}", formatBigDecimal(t));
            replacements.put("{YOUR_TICKETS%}", formatDouble(getPercent(t, s)));
            replacements.put("{TIME}", time);
            sendStringListMessage(sender, config.getStringList("messages.view"), replacements);
        }
    }
    public void viewStats(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.jackpot.stats", true)) {
            final HashMap<String, String> replacements = new HashMap<>();
            final JackpotStats pdata = FileRSPlayer.get(player.getUniqueId()).getJackpotStats();
            replacements.put("{$}", formatBigDecimal(pdata.getWonCash()));
            replacements.put("{TICKETS}", formatBigDecimal(pdata.getTicketsPurchased()));
            replacements.put("{WINS}", formatBigDecimal(pdata.getTimesWon()));
            sendStringListMessage(player, config.getStringList("messages.stats"), replacements);
        }
    }
    public void viewTop(@NotNull CommandSender sender, int page) {
        if(hasPermission(sender, "RandomSky.jackpot.top", true)) {
            final List<String> list = colorizeListString(config.getStringList("messages.top"));
            final String p = Integer.toString(page);
            final int perPage = config.getInt("messages.players per page");
            for(String s : list) {
                if(s.contains("{PLACE}")) {
                    // TODO: fix dis
                } else {
                    s = s.replace("{PAGE}", p);
                    sender.sendMessage(s);
                }
            }
        }
    }
    public void viewHelp(@NotNull CommandSender sender) {
        if(hasPermission(sender, "RandomSky.jackpot.help", true)) {
            sendStringListMessage(sender, config.getStringList("messages.help"), null);
        }
    }
    public void tryToggleNotifications(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.jackpot.toggle", true)) {
            final JackpotStats stats = FileRSPlayer.get(player.getUniqueId()).getJackpotStats();
            final boolean status = !stats.receivesNotifications();
            stats.setReceivesNotifications(status);
            sendStringListMessage(player, config.getStringList("messages.toggle notifications." + (status ? "on" : "off")), null);
        }
    }

    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        purchasing.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final UUID u = player.getUniqueId();
        if(purchasing.containsKey(u)) {
            event.setCancelled(true);
            player.updateInventory();
            final int r = event.getRawSlot();
            final ItemStack c = event.getCurrentItem();
            if(r < 0 || r >= player.getOpenInventory().getTopInventory().getSize() || c == null || c.getType().equals(Material.AIR)) return;
            if(confirmSlots.contains(r)) {
                purchaseTickets(player, purchasing.get(u));
            } else if(cancelSlots.contains(r)) {
            } else return;
            player.closeInventory();
        }
    }
}
