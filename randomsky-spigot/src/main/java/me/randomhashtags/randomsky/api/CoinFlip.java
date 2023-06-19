package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.addon.obj.CoinFlipMatch;
import me.randomhashtags.randomsky.addon.obj.CoinFlipOption;
import me.randomhashtags.randomsky.addon.obj.CoinFlipStats;
import me.randomhashtags.randomsky.event.CoinFlipEndEvent;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.FileRSPlayer;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.universal.UInventory;
import me.randomhashtags.randomsky.universal.UMaterial;
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
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

public enum CoinFlip implements RSFeature, CommandExecutor {
    INSTANCE;

    public YamlConfiguration config;

    private boolean isLegacy;
    private UInventory gui, options, challenge;
    private int countdownStart;
    private ItemStack countdown;
    private double tax;
    private long minWager;
    private String wagerName, yourSelection, opponentSelection;
    private List<String> addedlore, wagerLore, wagerAvailable, wagerUnavailable, winnerLore, rollingLore;
    private List<CoinFlipMatch> available;

    private LinkedHashMap<Integer, CoinFlipOption> optionz;
    private HashMap<String, Integer> challengeSlots;
    private HashMap<OfflinePlayer, BigDecimal> picking;
    private HashMap<CoinFlipMatch, List<Integer>> tasks;
    private HashMap<Player, CoinFlipMatch> goingToChallenge, active;

    public String getIdentifier() { return "COIN_FLIP"; }

    @Override
    public @NotNull RandomSkyFeature get_feature() {
        return RandomSkyFeature.COIN_FLIP;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) return true;
        final Player player = (Player) sender;
        final int l = args.length;
        if(l == 0) {
            viewCoinFlips(player);
        } else {
            final String a = args[0];
            switch (a) {
                case "cancel":
                    tryCancelling(player);
                    break;
                case "stats":
                    viewStats(player);
                    break;
                case "toggle":
                case "stfu":
                    tryToggleNotifications(player);
                    break;
                case "help":
                    viewHelp(player);
                    break;
                default:
                    final long m = a.endsWith("k") ? 1000 : a.endsWith("m") ? 1000000 : a.endsWith("b") ? 1000000000 : 1;
                    final BigDecimal w = BigDecimal.valueOf(getRemainingDouble(a)*m);
                    if(w.doubleValue() <= 0) {
                        sendStringListMessage(player, getStringList(config, "messages.must enter valid amount"), null);
                    } else {
                        tryCreating(player, w);
                    }
                    break;
            }
        }
        return true;
    }

    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "coinflip.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "coinflip.yml"));

        isLegacy = EIGHT || NINE || TEN || ELEVEN;

        minWager = config.getLong("min wager");
        tax = config.getDouble("wager.tax");
        wagerAvailable = colorizeListString(getStringList(config, "wager.status.can afford"));
        wagerUnavailable = colorizeListString(getStringList(config, "wager.status.cannot afford"));
        wagerName = colorize(config.getString("wager.name"));
        wagerLore = colorizeListString(getStringList(config, "wager.lore"));

        yourSelection = colorize(config.getString("challenge.your selection"));
        opponentSelection = colorize(config.getString("challenge.opponent selection"));
        winnerLore = colorizeListString(getStringList(config, "challenge.winner.lore"));
        countdown = d(config, "challenge.countdown");
        rollingLore = colorizeListString(getStringList(config, "challenge.rolling.lore"));

        gui = new UInventory(null, 54, colorize(config.getString("gui.title")));
        options = new UInventory(null, config.getInt("gui.options.size"), colorize(config.getString("gui.options.title")));
        challenge = new UInventory(null, config.getInt("challenge.size"), colorize(config.getString("challenge.title")));
        countdownStart = config.getInt("gui.options.countdown");
        addedlore = colorizeListString(getStringList(config, "gui.options.added lore"));
        optionz = new LinkedHashMap<>();

        final Inventory oi = options.getInventory();
        for(String s : config.getConfigurationSection("gui.options").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("countdown") && !s.equals("added lore")) {
                final String p = "gui.options." + s + ".";
                final int slot = config.getInt(p + "slot");
                final ItemStack dis = d(config, "gui.options." + s);
                final ItemMeta itemMeta = dis.getItemMeta();
                itemMeta.setLore(addedlore);
                dis.setItemMeta(itemMeta);
                final CoinFlipOption o = new CoinFlipOption(s, slot, colorize(config.getString(p + "chosen")), dis, d(config, p + "selection"), colorize(config.getString(p + "selection.color")));
                optionz.put(slot, o);
                oi.setItem(slot, dis);
            }
        }

        challengeSlots = new HashMap<>();
        challengeSlots.put("creator", config.getInt("challenge.creator.slot"));
        challengeSlots.put("creatorSelection", config.getInt("challenge.creator selection.slot"));
        challengeSlots.put("winner", config.getInt("challenge.winner.slot"));
        challengeSlots.put("challengerSelection", config.getInt("challenge.challenger selection.slot"));
        challengeSlots.put("challenger", config.getInt("challenge.challenger.slot"));

        picking = new HashMap<>();
        tasks = new HashMap<>();
        goingToChallenge = new HashMap<>();
        active = new HashMap<>();
        available = new ArrayList<>();

        final YamlConfiguration a = otherdata;
        final ConfigurationSection c = a.getConfigurationSection("coinflips");
        if(c != null) {
            for(String s : c.getKeys(false)) {
                final CoinFlipMatch m = new CoinFlipMatch(a.getLong("coinflips." + s + ".created"), Bukkit.getOfflinePlayer(UUID.fromString(s)), CoinFlipOption.paths.get(a.getString("coinflips." + s + ".option")), getBigDecimal(a.getString("coinflips." + s + ".wager")));
                available.add(m);
            }
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded Coin Flip &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        for(OfflinePlayer p : picking.keySet()) {
            if(p.isOnline()) {
                p.getPlayer().closeInventory();
            }
        }
        for(CoinFlipMatch m : tasks.keySet()) {
            for(int i : tasks.get(m)) {
                SCHEDULER.cancelTask(i);
            }
        }
        final YamlConfiguration a = otherdata;
        a.set("coinflips", null);
        for(CoinFlipMatch m : available) {
            final String u = m.creator().getUniqueId().toString();
            a.set("coinflips." + u + ".created", m.created());
            a.set("coinflips." + u + ".wager", m.wager());
            a.set("coinflips." + u + ".option", m.option().path);
            m.delete();
        }
        saveOtherData();
        for(Player p : active.keySet()) {
            p.closeInventory();
        }
        CoinFlipOption.paths = null;
        CoinFlipMatch.matches = null;
    }

    public void viewCoinFlips(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.coinflip.view", true)) {
            player.closeInventory();
            final int size = ((available.size()+9)/9)*9;
            player.openInventory(Bukkit.createInventory(player, size, gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            final double bal = ECONOMY.getBalance(player);
            for(CoinFlipMatch m : available) {
                final ItemStack item = UMaterial.PLAYER_HEAD_ITEM.getItemStack();
                final SkullMeta s = (SkullMeta) item.getItemMeta();
                final OfflinePlayer c = m.creator();
                if(isLegacy) {
                    s.setOwner(c.getName());
                } else {
                    s.setOwningPlayer(c);
                }
                s.setDisplayName(wagerName.replace("{PLAYER}", c.getName()));
                final BigDecimal wager = m.wager();
                final double wd = wager.doubleValue();
                final String w = formatBigDecimal(wager), tax = formatBigDecimal(BigDecimal.valueOf(wd*this.tax)), ch = m.option().chosen;
                final List<String> lore = new ArrayList<>();
                for(String l : wagerLore) {
                    if(l.equals("{STATUS}")) {
                        lore.addAll(bal >= wd ? wagerAvailable : wagerUnavailable);
                    } else {
                        lore.add(l.replace("{WAGER}", w).replace("{TAX}", tax).replace("{CHOSEN}", ch));
                    }
                }
                s.setLore(lore);
                item.setItemMeta(s);
                top.setItem(top.firstEmpty(), item);
            }
            player.updateInventory();
        }
    }
    public void viewStats(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.coinflip.stats", true)) {
            final HashMap<String, String> replacements = new HashMap<>();
            final FileRSPlayer pdata = FileRSPlayer.get(player.getUniqueId());
            final CoinFlipStats s = pdata.getCoinFlipStats();
            replacements.put("{WINS}", formatBigDecimal(s.wins));
            replacements.put("{LOSSES}", formatBigDecimal(s.losses));
            replacements.put("{WON$}", formatBigDecimal(s.cash_won));
            replacements.put("{LOST$}", formatBigDecimal(s.cash_lost));
            replacements.put("{TAXES}", formatBigDecimal(s.taxes_paid));
            sendStringListMessage(player, getStringList(config, "messages.stats"), replacements);
        }
    }
    public void tryToggleNotifications(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.coinflip.toggle", true)) {
            final FileRSPlayer pdata = FileRSPlayer.get(player.getUniqueId());
            final CoinFlipStats stats = pdata.getCoinFlipStats();
            final boolean status = !stats.receives_notifications;
            stats.receives_notifications = status;
            sendStringListMessage(player, getStringList(config, "messages.toggle notifications." + (status ? "on" : "off")), null);
        }
    }
    public void viewHelp(@NotNull CommandSender sender) {
        if(hasPermission(sender, "RandomSky.coinflip.help", true)) {
            sendStringListMessage(sender, getStringList(config, "messages.help"), null);
        }
    }
    public void tryCreating(@NotNull Player player, @NotNull  BigDecimal w) {
        if(hasPermission(player, "RandomSky.coinflip.create", true)) {
            final CoinFlipMatch m = CoinFlipMatch.valueOf(player);
            if(m != null) {
                sendStringListMessage(player, getStringList(config, "messages.already in a match"), null);
            } else {
                final double b = ECONOMY.getBalance(player), wager = w.doubleValue();
                final HashMap<String, String> replacements = new HashMap<>();
                if(b < wager) {
                    replacements.put("{BAL}", formatDouble(b));
                    sendStringListMessage(player, getStringList(config, "messages.cannot afford"), replacements);
                } else if(wager < minWager) {
                    replacements.put("{MIN}", formatLong(minWager));
                    sendStringListMessage(player, getStringList(config, "messages.wager needs to be more"), replacements);
                } else {
                    final String ww = formatBigDecimal(w);
                    player.closeInventory();
                    final int size = options.getSize();
                    player.openInventory(Bukkit.createInventory(player, size, options.getTitle()));
                    final Inventory top = player.getOpenInventory().getTopInventory();
                    top.setContents(options.getInventory().getContents());
                    for(int i = 0; i < size; i++) {
                        final ItemStack item = top.getItem(i);
                        if(item != null) {
                            final ItemMeta itemMeta = item.getItemMeta();
                            final List<String> l = itemMeta.getLore();
                            final List<String> lore = new ArrayList<>();
                            if(l != null) {
                                for(String s : l) {
                                    lore.add(s.replace("{WAGER}", ww));
                                }
                            }
                            itemMeta.setLore(lore);
                            item.setItemMeta(itemMeta);
                        }
                    }
                    player.updateInventory();
                    picking.put(player, w);
                }
            }
        }
    }
    public void tryCancelling(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.coinflip.cancel", true)) {
            final CoinFlipMatch m = CoinFlipMatch.valueOf(player);
            if(m == null) {
                sendStringListMessage(player, getStringList(config, "messages.cancel dont have one"), null);
            } else {
                final BigDecimal a = m.wager();
                ECONOMY.depositPlayer(player, a.doubleValue());
                delete(m);
                sendStringListMessage(player, getStringList(config, "messages.cancelled"), null);
            }
        }
    }
    public void tryChallenging(@NotNull Player player, @NotNull CoinFlipMatch match) {
        if(hasPermission(player, "RandomSky.coinflip.challenge", true)) {
            player.closeInventory();
            final CoinFlipMatch f = CoinFlipMatch.valueOf(player);
            if(f != null) {
                sendStringListMessage(player, getStringList(config, "messages.already in a match"), null);
            } else if(match != null) {
                if(match.isActive) {
                    player.closeInventory();
                    sendStringListMessage(player, getStringList(config, "messages.no longer available"), null);
                    viewCoinFlips(player);
                } else {
                    final String w = formatBigDecimal(match.wager());
                    final int s = options.getSize();
                    player.openInventory(Bukkit.createInventory(player, s, options.getTitle()));
                    final Inventory top = player.getOpenInventory().getTopInventory();
                    top.setContents(options.getInventory().getContents());
                    top.setItem(match.option().slot, new ItemStack(Material.AIR));
                    for(int i = 0; i < s; i++) {
                        final ItemStack item = top.getItem(i);
                        if(item != null) {
                            final ItemMeta itemMeta = item.getItemMeta();
                            final List<String> lore = new ArrayList<>();
                            if(itemMeta.hasLore()) {
                                for(String l : itemMeta.getLore()) {
                                    lore.add(l.replace("{WAGER}", w));
                                }
                            }
                            itemMeta.setLore(lore);
                            item.setItemMeta(itemMeta);
                        }
                    }
                    player.updateInventory();
                    goingToChallenge.put(player, match);
                }
            }
        }
    }
    private ItemStack getSelection(OfflinePlayer player, OfflinePlayer target, CoinFlipMatch m) {
        final boolean isCreator = target.equals(m.creator());
        final CoinFlipOption o = isCreator ? m.option() : m.challengerOption;
        final ItemStack item = o.selection();
        final ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName((player.equals(target) ? yourSelection : opponentSelection).replace("{COLOR}", o.selectionColor).replace("{PLAYER}", target.getName()));
        item.setItemMeta(itemMeta);
        return item;
    }
    private void start(CoinFlipMatch m) {
        available.remove(m);
        m.isActive = true;
        tasks.put(m, new ArrayList<>());

        final CoinFlipOption l = m.option(), r = m.challengerOption;
        final OfflinePlayer a = m.creator(), b = m.challenger;
        final String w = formatBigDecimal(m.wager()), an = a.getName(), bn = b.getName(), c = l.selectionColor, cc = r.selectionColor, cd = Integer.toString(countdownStart), T = challenge.getTitle().replace("{WAGER}", w);
        final int size = challenge.getSize();
        final Inventory inv = Bukkit.createInventory(null, size, T);
        final ItemStack creator = UMaterial.PLAYER_HEAD_ITEM.getItemStack(), challenger = creator.clone();
        final SkullMeta y = (SkullMeta) creator.getItemMeta(), z = (SkullMeta) challenger.getItemMeta();
        if(isLegacy) y.setOwner(a.getName());
        else y.setOwningPlayer(a);
        y.setDisplayName(c+an);
        z.setDisplayName(cc+bn);
        if(isLegacy) z.setOwner(b.getName());
        else z.setOwningPlayer(b);
        creator.setItemMeta(y);
        challenger.setItemMeta(z);

        final int Y = challengeSlots.get("creatorSelection"), Z = challengeSlots.get("challengerSelection");

        inv.setItem(challengeSlots.get("creator"), creator);
        inv.setItem(Y, getSelection(a, a, m));
        inv.setItem(Z, getSelection(a, b, m));
        inv.setItem(challengeSlots.get("challenger"), challenger);

        final ItemStack item = countdown.clone();
        final ItemMeta itemMeta = item.getItemMeta();
        item.setAmount(countdownStart);
        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{TIME}", cd));
        final List<String> lore = new ArrayList<>();
        for(String s : itemMeta.getLore()) {
            lore.add(s.replace("{TIME}", cd));
        }
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        inv.setItem(challengeSlots.get("winner"), item);

        final ItemStack[] contents = inv.getContents();

        if(a.isOnline()) {
            final Player p = a.getPlayer();
            p.closeInventory();
            p.openInventory(Bukkit.createInventory(p, size, T));
            p.getOpenInventory().getTopInventory().setContents(contents);
            p.updateInventory();
            active.put(p, m);
        }
        if(b.isOnline()) {
            final Player p = b.getPlayer();
            p.closeInventory();
            p.openInventory(Bukkit.createInventory(p, size, T));
            final Inventory TOP = p.getOpenInventory().getTopInventory();
            TOP.setContents(contents);
            TOP.setItem(Y, getSelection(b, a, m));
            TOP.setItem(Z, getSelection(b, b, m));
            p.updateInventory();
            active.put(p, m);
        }

        final String rollingName = colorize(config.getString("challenge.rolling.name"));
        final List<Integer> t = tasks.get(m);
        for(int i = 1; i <= countdownStart; i++) {
            final int I = i;
            t.add(SCHEDULER.scheduleSyncDelayedTask(RANDOM_SKY, () -> {
                final String CD = Integer.toString(countdownStart-I);
                item = countdown.clone();
                itemMeta = item.getItemMeta();
                lore.clear();
                item.setAmount(countdownStart-I);
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{TIME}", CD));
                for(String s : itemMeta.getLore()) {
                    lore.add(s.replace("{TIME}", CD));
                }
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
                final Player A = a.getPlayer(), B = b.getPlayer();
                final int q = challengeSlots.get("winner");
                if(a.isOnline() && active.containsKey(A)) {
                    A.getOpenInventory().getTopInventory().setItem(q, item);
                    A.updateInventory();
                }
                if(b.isOnline() && active.containsKey(B)) {
                    B.getOpenInventory().getTopInventory().setItem(q, item);
                    B.updateInventory();
                }
                if(I == countdownStart) {
                    final CoinFlipOption op1 = m.option(), op2 = m.challengerOption;
                    final String color1 = op1.selectionColor, color2 = op2.selectionColor;
                    final ItemStack F = op1.selection(), G = op2.selection();
                    ItemStack option = F;
                    String selectionColor = color1;
                    for(int o = 0; o <= 60; o++) {
                        final int d = o*2;
                        if(o == 60) {
                            t.add(SCHEDULER.scheduleSyncDelayedTask(RANDOM_SKY, () -> chooseWinner(m), d));
                        } else {
                            item = option.clone(); itemMeta = item.getItemMeta();
                            itemMeta.setDisplayName(rollingName.replace("{SELECTION_COLOR}", selectionColor));
                            itemMeta.setLore(rollingLore);
                            item.setItemMeta(itemMeta);
                            t.add(SCHEDULER.scheduleSyncDelayedTask(RANDOM_SKY, () -> {
                                if(a.isOnline() && active.containsKey(A)) {
                                    A.getOpenInventory().getTopInventory().setItem(q, item);
                                }
                                if(b.isOnline() && active.containsKey(B)) {
                                    B.getOpenInventory().getTopInventory().setItem(q, item);
                                }
                            }, d));
                            final boolean isF = option == F;
                            option = isF ? G : F;
                            selectionColor = isF ? color2 : color1;
                        }
                    }

                }
            }, 20*i));
        }
    }
    private void chooseWinner(CoinFlipMatch m) {
        available.remove(m);
        m.isActive = false;
        final OfflinePlayer a = m.creator(), b = m.challenger;
        final CoinFlipOption l = m.option(), r = m.challengerOption;
        final BigDecimal wager = m.wager(), t = BigDecimal.valueOf(wager.doubleValue()*tax), total = BigDecimal.valueOf(wager.doubleValue()*2), taxed = BigDecimal.valueOf(total.doubleValue()*(tax*2));
        final boolean zero = RANDOM.nextInt(2) == 0;
        final CoinFlipOption winningOption = zero ? l : r, losingOption = zero ? r : l;
        final OfflinePlayer winner = zero ? a : b, loser = winner == a ? b : a;
        final FileRSPlayer W = FileRSPlayer.get(winner.getUniqueId()), L = FileRSPlayer.get(loser.getUniqueId());

        final CoinFlipStats s1 = W.getCoinFlipStats(), s2 = L.getCoinFlipStats();
        s1.cash_won = s1.cash_won.add(t);
        s1.wins = s1.wins.add(BigDecimal.ONE);
        s1.taxes_paid = s1.taxes_paid.add(taxed);
        s2.cash_lost = s2.cash_lost.add(t);
        s2.losses = s2.losses.add(BigDecimal.ONE);
        s2.taxes_paid = s2.taxes_paid.add(taxed);

        if(!W.isOnline()) {
            W.unload();
        }
        if(!L.isOnline()) {
            L.unload();
        }
        final Player ap = a.getPlayer(), bp = b.getPlayer();
        if(active.containsKey(ap)) {
            active.put(ap, null);
        }
        if(active.containsKey(bp)) {
            active.put(bp, null);
        }
        final String winnerName = winner.getName(), color = winningOption.selectionColor, Lcolor = losingOption.selectionColor;
        ECONOMY.depositPlayer(winner, total.doubleValue()-taxed.doubleValue());
        final ItemStack item = winningOption.appear();
        final ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(colorize(config.getString("challenge.winner.name")).replace("{COLOR}", color).replace("{PLAYER}", winnerName));
        final List<String> lore = new ArrayList<>();
        for(String s : winnerLore) {
            lore.add(s.replace("{PLAYER}", winnerName).replace("{COLOR}", color));
        }
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        final int slot = challengeSlots.get("winner");
        if(a.isOnline() && active.containsKey(ap)) {
            final Player p = a.getPlayer();
            p.getOpenInventory().getTopInventory().setItem(slot, item);
            p.updateInventory();
        }
        if(b.isOnline() && active.containsKey(bp)) {
            final Player p = b.getPlayer();
            p.getOpenInventory().getTopInventory().setItem(slot, item);
            p.updateInventory();
        }
        stopTasks(m);
        m.delete();

        final List<String> w = colorizeListString(getStringList(config, "messages.winner"));
        final HashMap<String, String> replacements = new HashMap<>();
        replacements.put("{WINNING_COLOR}", color);
        replacements.put("{LOSING_COLOR}", Lcolor);
        replacements.put("{WINNER}", winnerName);
        replacements.put("{LOSER}", loser.getName());
        replacements.put("{WAGER}", formatBigDecimal(total));
        for(String s : w) {
            for(String re : replacements.keySet()) s = s.replace(re, replacements.get(re));
            sendConsoleMessage(s);
        }
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(FileRSPlayer.get(p.getUniqueId()).getCoinFlipStats().receives_notifications) {
                sendStringListMessage(p, w, replacements);
            }
        }
        final CoinFlipEndEvent e = new CoinFlipEndEvent(winner.getUniqueId(), loser.getUniqueId(), wager, BigDecimal.valueOf(tax));
        PLUGIN_MANAGER.callEvent(e);
    }
    private void stopTasks(@NotNull CoinFlipMatch m) {
        if(tasks.containsKey(m)) {
            for(int i : tasks.get(m)) {
                SCHEDULER.cancelTask(i);
            }
            tasks.remove(m);
        }
    }
    private void delete(@NotNull CoinFlipMatch m) {
        stopTasks(m);
        available.remove(m);
        m.delete();
    }
    public void create(OfflinePlayer player, CoinFlipOption picked, boolean withdraw, boolean sendMsg) {
        final BigDecimal wager = picking.get(player);
        picking.remove(player);
        if(withdraw) ECONOMY.withdrawPlayer(player, wager.doubleValue());
        if(sendMsg && player.isOnline()) sendStringListMessage(player.getPlayer(), getStringList(config, "messages.created"), null);
        final CoinFlipMatch m = new CoinFlipMatch(System.currentTimeMillis(), player, picked, wager);
        available.add(m);
    }

    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        picking.remove(player);
        goingToChallenge.remove(player);
        if(active.containsKey(player)) {
            final CoinFlipMatch m = active.get(player);
            active.remove(player);
            if(m != null && m.isActive && !active.containsKey(m.challenger.getPlayer())) {
                chooseWinner(m);
                m.delete();
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        final boolean p = picking.containsKey(player), ch = goingToChallenge.containsKey(player), g = event.getView().getTitle().equals(gui.getTitle()), a = active.containsKey(player);
        if(p || ch || g || a) {
            event.setCancelled(true);
            player.updateInventory();
            final int r = event.getRawSlot();
            final ItemStack c = event.getCurrentItem();
            if(r < 0 || r > top.getSize() || c == null || c.getType().equals(Material.AIR) || a) return;

            if(g) {
                final SkullMeta m = (SkullMeta) c.getItemMeta();
                final OfflinePlayer n = isLegacy ? Bukkit.getOfflinePlayer(m.getOwner()) : m.getOwningPlayer();
                final CoinFlipMatch f = CoinFlipMatch.valueOf(n);
                tryChallenging(player, f);
            } else if(ch) {
                final CoinFlipMatch f = goingToChallenge.get(player);
                final double b = ECONOMY.getBalance(player);
                final BigDecimal w = f.wager();
                if(b < w.doubleValue()) {
                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("{BAL}", formatDouble(b));
                    sendStringListMessage(player, getStringList(config, "messages.cannot afford"), replacements);
                } else {
                    player.closeInventory();
                    if(!available.contains(f) || f.isActive) {
                        sendStringListMessage(player, getStringList(config, "messages.no longer available"), null);
                        viewCoinFlips(player);
                    } else if(optionz.containsKey(r)) {
                        ECONOMY.withdrawPlayer(player, w.doubleValue());
                        f.challenger = player;
                        f.challengerOption = optionz.get(r);
                        start(f);
                    }
                }
            } else {
                final CoinFlipOption o = optionz.getOrDefault(r, null);
                if(o != null) {
                    create(player, o, true, true);
                    player.closeInventory();
                }
            }
        }
    }
}
