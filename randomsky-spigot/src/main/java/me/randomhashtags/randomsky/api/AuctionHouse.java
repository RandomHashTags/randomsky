package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.addon.AuctionViewType;
import me.randomhashtags.randomsky.addon.obj.AuctionedItemObj;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

import static java.io.File.separator;
import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.toMap;

public enum AuctionHouse implements RSFeature, CommandExecutor {
    INSTANCE;


    public YamlConfiguration config;

    private File dataF;
    private YamlConfiguration data;

    private long collectionbinExpiration, auctionExpiration;
    public UInventory ah, categories, collectionbin, confirmAuction, purchaseItem, categoryItems;
    private ItemStack previousPage, nextPage, refresh, categoryView, collectionBin, returnToAH, mainCategoryView;
    private int previousPageSlot, nextPageSlot;
    private List<String> clickToBuyStatus, cancelStatus, format, categoryFormat, collectionBinInAuction, collectionBinClaim;
    private String organization;

    private HashMap<Player, AuctionedItemObj> purchasing;
    private List<Integer> confirmAuctionSlots, cancelAuctionSlots, confirmPurchaseSlots, cancelPurchaseSlots, slots;

    private HashMap<Player, HashMap<ItemStack, BigDecimal>> auctioning;

    public HashMap<UUID, List<AuctionedItemObj>> auctions;
    public HashMap<Long, AuctionedItemObj> auctionHouse;
    public HashMap<UMaterial, HashMap<String, List<AuctionedItemObj>>> category;
    public HashMap<Player, Integer> page;
    public HashMap<Player, AuctionViewType> viewing;
    public HashMap<Player, UMaterial> viewingCategory;

    private HashMap<AuctionedItemObj, Integer> task;

    public String getIdentifier() { return "AUCTION_HOUSE"; }

    @Override
    public @NotNull RandomSkyFeature get_feature() {
        return RandomSkyFeature.AUCTION_HOUSE;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final boolean i = player != null;
        final int l = args.length;
        if(l == 0) {
            if(i) view(player, 1);
        } else {
            final String arg0 = args[0], arg1 = l >= 2 ? args[1] : null;
            switch (arg0) {
                case "sell":
                    if(arg1 != null) {
                        final BigDecimal price = BigDecimal.valueOf(getRemainingDouble(arg1));
                        final ItemStack is = player.getItemInHand();
                        if(is.getType().equals(Material.AIR)) {
                            sendStringListMessage(player, getStringList(config, "messages.need to be holding item"), null);
                        } else if(price.doubleValue() <= 0.00) {
                            sendStringListMessage(player, getStringList(config, "messages.must enter valid price"), null);
                        } else {
                            confirmAuction(player, is, price);
                        }
                    }
                    break;
                case "collect":
                    viewCollectionBin(player);
                    break;
                case "help":
                    viewHelp(sender);
                    break;
                default:
                    if(i) view(player, 1);
                    break;
            }
        }
        return true;
    }

    public void load() {
        save(null, "auction house.yml");
        config = YamlConfiguration.loadConfiguration(new File(dataF, "auction house.yml"));
        save("_Data", "auctions.yml");
        dataF = new File(dataF + separator + "_Data", "auctions.yml");
        data = YamlConfiguration.loadConfiguration(dataF);

        purchasing = new HashMap<>();
        confirmAuctionSlots = new ArrayList<>();
        cancelAuctionSlots = new ArrayList<>();
        confirmPurchaseSlots = new ArrayList<>();
        cancelPurchaseSlots = new ArrayList<>();
        slots = new ArrayList<>();
        auctioning = new HashMap<>();
        auctions = new HashMap<>();
        auctionHouse = new HashMap<>();
        category = new HashMap<>();
        page = new HashMap<>();
        viewing = new HashMap<>();
        viewingCategory = new HashMap<>();
        task = new HashMap<>();

        final ItemStack air = new ItemStack(Material.AIR);
        final String[] itemslots = config.getString("auction house.item slots").split("-");
        for(int i = Integer.parseInt(itemslots[0]); i <= Integer.parseInt(itemslots[1]); i++) {
            slots.add(i);
        }

        organization = config.getString("auction house.organization");
        auctionExpiration = config.getLong("auction house.auction expiration")*1000;
        collectionbinExpiration = config.getLong("auction house.collection bin expiration")*1000;
        format = colorizeListString(getStringList(config, "auction house.format"));
        clickToBuyStatus = colorizeListString(getStringList(config, "auction house.status.click to buy"));
        cancelStatus = colorizeListString(getStringList(config, "auction house.status.cancel"));
        categoryView = d(config, "auction house.category view");
        collectionBin = d(config, "player collection bin");
        returnToAH = d(config, "return to ah");
        categoryFormat = colorizeListString(getStringList(config, "categories.format"));
        mainCategoryView = d(config, "category items.main category view");
        collectionBinClaim = colorizeListString(getStringList(config, "collection bin.claim"));
        collectionBinInAuction = colorizeListString(getStringList(config, "collection bin.in auction"));

        ah = new UInventory(null, config.getInt("auction house.size"), colorize(config.getString("auction house.title")));
        previousPage = d(config, "auction house.previous page");
        previousPageSlot = config.getInt("auction house.previous page.slot");
        nextPage = d(config, "auction house.next page");
        nextPageSlot = config.getInt("auction house.next page.slot");
        refresh = d(config, "refresh");

        final Inventory ahi = ah.getInventory();
        for(String s : config.getConfigurationSection("auction house").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("item slots") && !s.equals("organization") && !s.equals("auction expiration") && !s.equals("collection bin expiration") && !s.equals("format") && !s.equals("status") && !s.equals("previous page") && !s.equals("next page")) {
                final String i = config.getString("auction house." + s + ".item");
                final ItemStack item = i.equals("{REFRESH}") ? refresh : i.equals("{COLLECTION_BIN}") ? collectionBin : d(config, "auction house." + s);
                ahi.setItem(config.getInt("auction house." + s + ".slot"), item);
            }
        }

        ahi.setItem(previousPageSlot, previousPage);
        ahi.setItem(nextPageSlot, nextPage);

        purchaseItem = new UInventory(null, config.getInt("purchase item.size"), colorize(config.getString("purchase item.title")));
        final Inventory pii = purchaseItem.getInventory();
        final ItemStack confirmPurchase = d(config, "purchase item.confirm"), cancelPurchase = d(config, "purchase item.cancel");
        for(String s : config.getConfigurationSection("purchase item").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("confirm") && !s.equals("cancel")) {
                final String i = config.getString("purchase item." + s + ".item").toLowerCase();
                final boolean isC = i.equals("confirm"), isCa = i.equals("cancel"), isI = i.equals("{item}");
                final ItemStack item = isC ? confirmPurchase : isCa ? cancelPurchase : isI ? air : d(config, "purchase item." + s);
                final int slot = config.getInt("purchase item." + s + ".slot");
                if(isC) confirmPurchaseSlots.add(slot);
                else if(isCa) cancelPurchaseSlots.add(slot);
                pii.setItem(slot, item);
            }
        }

        confirmAuction = new UInventory(null, config.getInt("confirm auction.size"), colorize(config.getString("confirm auction.title")));
        final Inventory cai = confirmAuction.getInventory();
        final ItemStack confirmAuctionAccept = d(config, "confirm auction.accept"), confirmAuctionDecline = d(config, "confirm auction.decline");
        for(String s : config.getConfigurationSection("confirm auction").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("accept") && !s.equals("decline")) {
                final String i = config.getString("confirm auction." + s + ".item");
                final int slot = config.getInt("confirm auction." + s + ".slot");
                final boolean accept = i.equals("accept"), decline = i.equals("decline"), isI = i.equals("{ITEM}");
                final ItemStack item = isI ? air : accept ? confirmAuctionAccept : decline ? confirmAuctionDecline : d(config, "confirm auction." + s);
                if(accept) confirmAuctionSlots.add(slot);
                else if(decline) cancelAuctionSlots.add(slot);
                cai.setItem(slot, item);
            }
        }

        categories = new UInventory(null, config.getInt("categories.size"), colorize(config.getString("categories.title")));
        final Inventory ci = categories.getInventory();
        for(String s : config.getConfigurationSection("categories").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("format") && !s.equals("groups")) {
                final int slot = config.getInt("categories." + s + ".slot");
                final String t = config.getString("categories." + s + ".item").toLowerCase();
                final ItemStack item = t.equals("{refresh}") ? refresh : t.equals("{collection_bin}") ? collectionBin : t.equals("{return_to_ah}") ? returnToAH : d(config, "categories." + s);
                ci.setItem(slot, item);
            }
        }

        categoryItems = new UInventory(null, config.getInt("category items.size"), colorize(config.getString("category items.title")));
        final Inventory cii = categoryItems.getInventory();
        for(String s : config.getConfigurationSection("category items").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size")) {
                final int slot = config.getInt("category items." + s + ".slot");
                final String i = config.getString("category items." + s + ".item").toLowerCase();
                final ItemStack item = i.equals("{collection_bin}") ? collectionBin : d(config, "category items." + s);
                cii.setItem(slot, item);
            }
        }

        collectionbin = new UInventory(null, config.getInt("collection bin.size"), colorize(config.getString("collection bin.title")));
        final Inventory cbi = collectionbin.getInventory();
        for(String s : config.getConfigurationSection("collection bin").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("not enough inventory space") && !s.equals("in auction") && !s.equals("claim")) {
                final int slot = config.getInt("collection bin." + s + ".slot");
                final String i = config.getString("collection bin." + s + ".item").toLowerCase();
                final ItemStack item = i.equals("{refresh}") ? refresh : i.equals("{return_to_ah}") ? returnToAH : d(config, "collection bin." + s);
                cbi.setItem(slot, item);
            }
        }
        loadAuctions(true);
    }

    private void loadAuctions(boolean async) {
        if(async) {
            SCHEDULER.runTaskAsynchronously(RANDOM_SKY, () -> loadAH(true));
        } else {
            loadAH(false);
        }
    }
    private void loadAH(boolean async) {
        if(!is_enabled()) {
            return;
        }
        final ConfigurationSection au = data.getConfigurationSection("auctions");
        int ah = 0, cb = 0, d = 0;
        if(au != null) {
            final long now = System.currentTimeMillis();
            for(String uuid : au.getKeys(false)) {
                final UUID u = UUID.fromString(uuid);
                auctions.put(u, new ArrayList<>());
                final List<AuctionedItemObj> p = auctions.get(u);
                for(String a : data.getConfigurationSection("auctions." + uuid).getKeys(false)) {
                    final long l = Long.parseLong(a);
                    final ItemStack i = data.getItemStack("auctions." + uuid + "." + a + ".item");
                    final AuctionedItemObj ai = new AuctionedItemObj(l, u, i, BigDecimal.valueOf(data.getDouble("auctions." + uuid + "." + a + ".price")));
                    ai.setClaimable(data.getBoolean("auctions." + uuid + "." + a + ".claimable"));
                    final boolean claimable = ai.isClaimable();
                    boolean deleted = false;
                    if(claimable && now >= l+collectionbinExpiration) {
                        ai.setClaimable(false);
                        deleted = true;
                    } else if(!claimable && now >= l+auctionExpiration) {
                        ai.setClaimable(true);
                    }
                    if(deleted) {
                        d++;
                    } else {
                        p.add(ai);
                        if(ai.isClaimable()) {
                            cb++;
                        } else {
                            auctionHouse.put(l, ai);
                            ah++;
                            addToCategoryView(ai, UMaterial.match(i));
                        }
                    }
                }
            }
        }
        organizeAH();
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + ah + " Auctioned Items, " + cb + " Collection Bin items, and deleted " + d + " expired items " + (async ? "&e[async]" : ""));
    }
    private void organizeAH() {
        auctionHouse = auctionHouse.entrySet().stream().sorted(organization.equals("OLDEST") ? comparingByKey() : Collections.reverseOrder(comparingByKey())).collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
        category = category.entrySet().stream().sorted(comparingByKey()).collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
    }

    private void save() {
        try {
            data.save(dataF);
            data = YamlConfiguration.loadConfiguration(dataF);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void backup(boolean async) {
        if(async) SCHEDULER.runTaskAsynchronously(RANDOM_SKY, this::dobackup);
        else dobackup();
    }
    private void dobackup() {
        data.set("auctions", null);
        for(UUID u : auctions.keySet()) {
            final String s = u.toString(), p = "auctions." + s + ".";
            for(AuctionedItemObj a : auctions.get(u)) {
                final long l = a.getAuctionedTime();
                data.set(p + l + ".price", a.getPrice().doubleValue());
                data.set(p + l + ".claimable", a.isClaimable());
                data.set(p + l + ".item", a.getItem().toString());
            }
        }
        save();
    }

    public void unload() {
        backup(false);
        for(Player p : page.keySet()) {
            p.closeInventory();
        }
        for(Player p : viewingCategory.keySet()) {
            p.closeInventory();
        }
        for(AuctionedItemObj i : task.keySet()) {
            SCHEDULER.cancelTask(task.get(i));
        }
    }

    public void viewHelp(@NotNull CommandSender sender) {
        if(hasPermission(sender, "RandomSky.ah.help", true)) {
            sendStringListMessage(sender, getStringList(config, "messages.help"), null);
        }
    }

    public void updatePage(@NotNull Player player) {
        if(viewing.containsKey(player)) {
            final Inventory top = player.getOpenInventory().getTopInventory();
            final ItemStack air = new ItemStack(Material.AIR);
            for(int i : slots) {
                top.setItem(i, air);
            }
            final int p = page.get(player), S = auctionHouse.size()-1, starting = (p-1)*(slots.size()-1);
            final AuctionViewType v = viewing.get(player);
            switch (v) {
                case CATEGORY_VIEW:
                    int cat = 0, cate = 0;
                    HashMap<UMaterial, HashMap<String, List<AuctionedItemObj>>> y = new HashMap<>();
                    for(UMaterial u : category.keySet()) {
                        y.put(u, new HashMap<>());
                        cate += category.get(u).keySet().size();
                        for(String s : category.get(u).keySet()) {
                            if(cate > starting && slots.contains(cat)) {
                                y.get(u).put(s, category.get(u).get(s));
                                cat++;
                            }
                        }
                    }
                    y = y.entrySet().stream().sorted(comparingByKey()).collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
                    int slot = (int) slots.toArray()[0];
                    for(UMaterial u : y.keySet()) {
                        for(String s : y.get(u).keySet()) {
                            final String listings = Integer.toString(y.get(u).get(s).size());
                            BigDecimal lowestPrice = BigDecimal.ZERO;
                            for(AuctionedItemObj ai : y.get(u).get(s)) {
                                final BigDecimal price = ai.getPrice();
                                if(lowestPrice.equals(BigDecimal.ZERO) || price.doubleValue() < lowestPrice.doubleValue()) {
                                    lowestPrice = price;
                                }
                            }
                            final String lowest = formatBigDecimal(lowestPrice);
                            final ItemStack item = u.getItemStack();
                            final ItemMeta itemMeta = item.getItemMeta();
                            itemMeta.setDisplayName(s);
                            final List<String> lore = new ArrayList<>();
                            for(String x : categoryFormat) {
                                lore.add(x.replace("{LISTINGS}", listings).replace("{LOWEST_PRICE}", lowest));
                            }
                            itemMeta.setLore(lore);
                            item.setItemMeta(itemMeta);
                            top.setItem(slot, item);
                            if(slot+1 < slots.size()) slot = (int) slots.toArray()[slot+1];
                        }
                    }
                    setPages(v, cate, top, air, p);
                    break;
                case AUCTION_HOUSE:
                    setPages(v, 0, top, air, p);
                    final UUID u = player.getUniqueId();
                    int ahitem = starting+(p == 1 ? 0 : 1);
                    for(int i : slots) {
                        if(ahitem <= S) {
                            final long l = (long) auctionHouse.keySet().toArray()[ahitem];
                            final AuctionedItemObj a = auctionHouse.get(l);
                            final UUID auctioner = a.getAuctioner();
                            final String pr = formatBigDecimal(a.getPrice()), seller = Bukkit.getOfflinePlayer(auctioner).getName();
                            final ItemStack item = a.getItem();
                            final ItemMeta itemMeta = item.getItemMeta();
                            final List<String> lore = new ArrayList<>();
                            if(itemMeta.hasLore()) {
                                lore.addAll(itemMeta.getLore());
                            }
                            for(String s : format) {
                                if(s.equals("{STATUS}")) {
                                    lore.addAll(auctioner.equals(u) ? cancelStatus : clickToBuyStatus);
                                } else {
                                    lore.add(s.replace("{PRICE}", pr).replace("{SELLER}", seller));
                                }
                            }
                            itemMeta.setLore(lore);
                            item.setItemMeta(itemMeta);
                            top.setItem(i, item);
                            ahitem++;
                        }
                    }
                    break;
                case COLLECTION_BIN:
                    final List<AuctionedItemObj> cb = getCollectionBin(player);
                    slot = (int) slots.toArray()[0];
                    for(AuctionedItemObj a : cb) {
                        if(slots.contains(slot)) {
                            final String price = formatBigDecimal(a.getPrice());
                            final ItemStack item = a.getItem();
                            final ItemMeta itemMeta = item.getItemMeta();
                            final List<String> lore = new ArrayList<>();
                            if(itemMeta.hasLore()) {
                                lore.addAll(itemMeta.getLore());
                            }
                            final boolean claimable = a.isClaimable();
                            final List<String> type = claimable ? collectionBinClaim : collectionBinInAuction;
                            final String t = getRemainingTime(a.getAuctionedTime()+(claimable ? collectionbinExpiration : auctionExpiration)-System.currentTimeMillis());
                            for(String s : type) {
                                lore.add(s.replace("{PRICE}", price).replace("{TIME}", t));
                            }
                            itemMeta.setLore(lore);
                            item.setItemMeta(itemMeta);
                            top.setItem(slot, item);
                            if(slot+1 < slots.size()) {
                                slot = (int) slots.toArray()[slot+1];
                            } else {
                                slot = -1;
                            }
                        }
                    }
                    setPages(v, cb.size(), top, air, p);
                    break;
                default:
                    break;
            }
            for(int i = 0; i < top.getSize(); i++) {
                if(!slots.contains(i)) {
                    final ItemStack item = top.getItem(i);
                    if(item != null && item.equals(collectionBin)) {
                        top.setItem(i, getPlayerCollectionBin(player));
                    }
                }
            }
            player.updateInventory();
        }
    }
    public void updatePage(Player player, Inventory top, UMaterial material, String name) {
        viewingCategory.put(player, material);
        final UUID u = player.getUniqueId();
        int slot = (int) slots.toArray()[0];
        for(AuctionedItemObj a : category.get(material).get(name)) {
            final UUID auctioner = a.getAuctioner();
            final String price = formatBigDecimal(a.getPrice()), seller = Bukkit.getOfflinePlayer(auctioner).getName();
            final ItemStack item = a.getItem();
            final ItemMeta itemMeta = item.getItemMeta();
            final List<String> lore = new ArrayList<>();
            if(itemMeta.hasLore()) {
                lore.addAll(itemMeta.getLore());
            }
            for(String s : format) {
                if(s.equals("{STATUS}")) {
                    lore.addAll(auctioner.equals(u) ? cancelStatus : clickToBuyStatus);
                } else {
                    lore.add(s.replace("{PRICE}", price).replace("{SELLER}", seller));
                }
            }
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
            top.setItem(slot, item);
            if(slot+1 < slots.size()) {
                slot = (int) slots.toArray()[slot+1];
            }
        }
        player.updateInventory();
    }

    private void setPages(AuctionViewType type, int size, Inventory top, ItemStack air, int p) {
        final int maxpage = ((type == AuctionViewType.AUCTION_HOUSE ? auctionHouse.size() : size)/(slots.size()+1))+1;
        final String max = Integer.toString(maxpage);
        final ItemStack prev = p <= 1 ? air : previousPage.clone(), next = p < maxpage ? nextPage.clone() : air;
        if(prev != air) {
            final ItemMeta itemMeta = prev.getItemMeta();
            itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{PREV_PAGE}", Integer.toString(p-1)).replace("{MAX_PAGE}", max));
            final List<String> lore = new ArrayList<>();
            if(itemMeta.hasLore()) {
                lore.addAll(itemMeta.getLore());
            }
            itemMeta.setLore(lore);
            prev.setItemMeta(itemMeta);
            prev.setAmount(p-1);
        }
        if(next != air) {
            final ItemMeta itemMeta = next.getItemMeta();
            itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{NEXT_PAGE}", Integer.toString(p+1)).replace("{MAX_PAGE}", max));
            final List<String> lore = new ArrayList<>();
            if(itemMeta.hasLore()) {
                lore.addAll(itemMeta.getLore());
            }
            itemMeta.setLore(lore);
            next.setItemMeta(itemMeta);
            next.setAmount(p+1);
        }
        top.setItem(previousPageSlot, prev);
        top.setItem(nextPageSlot, next);
    }

    private void viewType(@NotNull Player player, @Nullable AuctionViewType type, int page, @NotNull UInventory gui) {
        viewType(player, type, page, gui, null, null);
    }
    private void viewType(@NotNull Player player, @Nullable AuctionViewType type, int page, @NotNull UInventory gui, @Nullable UMaterial material, String name) {
        player.closeInventory();
        if(page > 0) {
            this.page.put(player, page);
        }
        if(type != null) {
            viewing.put(player, type);
        }
        final int size = gui.getSize();
        player.openInventory(Bukkit.createInventory(player, size, gui.getTitle()));
        final Inventory top = player.getOpenInventory().getTopInventory();
        top.setContents(gui.getInventory().getContents());
        if(material != null) {
            updatePage(player, top, material, name);
        } else {
            updatePage(player);
        }
    }

    public void view(@NotNull Player player, int page) {
        if(hasPermission(player, "RandomSky.ah.view", true)) {
            viewType(player, AuctionViewType.AUCTION_HOUSE, page, ah);
        }
    }
    public void viewCategories(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.ah.view.categories", true)) {
            viewType(player, AuctionViewType.CATEGORY_VIEW, 1, categories);
        }
    }
    public void viewCategory(@NotNull Player player, @NotNull UMaterial material, @Nullable String name) {
        if(hasPermission(player, "RandomSky.ah.view.category", true)) {
            viewType(player, null, -1, categoryItems, material, name);
        }
    }
    public void viewCollectionBin(@NotNull Player player) {
        if(hasPermission(player, "RandomSky.ah.view.collectionbin", true)) {
            viewType(player, AuctionViewType.COLLECTION_BIN, 1, collectionbin);
        }
    }
    public void nextPage(@NotNull Player player) {
        if(viewing.containsKey(player)) {
            page.put(player, page.get(player)+1);
            updatePage(player);
        }
    }
    public void previousPage(@NotNull Player player) {
        if(viewing.containsKey(player)) {
            page.put(player, page.get(player)-1);
            updatePage(player);
        }
    }
    public void expire(@NotNull Player player, @NotNull AuctionedItemObj a) {
        final ItemStack i = a.getItem();
        if(a.isClaimable()) {
            giveItem(player, i);
            auctions.get(player.getUniqueId()).remove(a);
        } else {
            a.setClaimable(true);
            auctionHouse.remove(a.getAuctionedTime());
            category.get(UMaterial.match(i)).get(i.getItemMeta().getDisplayName()).remove(a);
            a.setAuctionTime(System.currentTimeMillis());
        }
        updatePage(player);
    }

    public void confirmAuction(@NotNull Player player, @NotNull ItemStack item, @NotNull BigDecimal price) {
        if(hasPermission(player, "RandomSky.ah.sell", true)) {
            final String p = formatBigDecimal(price);
            player.closeInventory();

            player.openInventory(Bukkit.createInventory(player, confirmAuction.getSize(), confirmAuction.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(confirmAuction.getInventory().getContents());
            final String i = toMaterial(item.getType().name(), false);

            for(ItemStack is : top.getContents()) {
                if(is != null) {
                    final ItemMeta itemMeta = is.getItemMeta();
                    final List<String> lore = new ArrayList<>();
                    if(itemMeta.hasLore()) {
                        for(String s : itemMeta.getLore()) {
                            s = s.replace("{PRICE}", p).replace("{ITEM}", i);
                            lore.add(s);
                        }
                    }
                    itemMeta.setLore(lore);
                    is.setItemMeta(itemMeta);
                }
            }
            top.setItem(top.firstEmpty(), item);
            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));
            player.updateInventory();
            auctioning.put(player, new HashMap<>());
            auctioning.get(player).put(item, price);
        }
    }
    public void auction(@NotNull Player player, @NotNull ItemStack item, @NotNull BigDecimal price) {
        if(hasPermission(player, "RandomSky.ah.auction", true)) {
            final UUID u = player.getUniqueId();
            if(!auctions.containsKey(u)) auctions.put(u, new ArrayList<>());
            final long l = System.currentTimeMillis();
            final UMaterial um = UMaterial.match(item);
            final AuctionedItemObj a = new AuctionedItemObj(l, u, item, price);
            auctions.get(u).add(a);
            auctionHouse.put(l, a);
            addToCategoryView(a, um);
            organizeAH();
            final String p = formatBigDecimal(price), i = item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : toMaterial(um.name(), false);
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{PRICE}", p);
            replacements.put("{ITEM}", i);
            sendStringListMessage(player, getStringList(config, "messages.listed"), replacements);
        }
    }
    private void addToCategoryView(AuctionedItemObj ai, UMaterial um) {
        final ItemStack i = ai.getItem();
        if(!category.containsKey(um)) category.put(um, new HashMap<>());
        final String dn = i.getItemMeta().hasDisplayName() ? i.getItemMeta().getDisplayName() : null;
        final HashMap<String, List<AuctionedItemObj>> m = category.get(um);
        if(!m.containsKey(dn)) m.put(dn, new ArrayList<>());
        m.get(dn).add(ai);
    }
    public void cancelAuction(@NotNull Player player, @NotNull AuctionedItemObj a) {
        auctionHouse.remove(a.getAuctionedTime());
        a.setClaimable(true);
        sendStringListMessage(player, getStringList(config, "messages.cancelled listing"), null);
    }
    public void tryPurchasing(@NotNull Player player, @NotNull AuctionedItemObj a) {
        if(hasPermission(player, "RandomSky.ah.buy", true)) {
            player.closeInventory();

            final ItemStack its = a.getItem();
            final String p = formatBigDecimal(a.getPrice()), it = its.hasItemMeta() && its.getItemMeta().hasDisplayName() ? its.getItemMeta().getDisplayName() : toMaterial(UMaterial.match(its).name(), false);
            final int size = purchaseItem.getSize();
            player.openInventory(Bukkit.createInventory(player, size, purchaseItem.getTitle().replace("{PRICE}", p)));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(purchaseItem.getInventory().getContents());
            for(int i = 0; i < size; i++) {
                final ItemStack item = top.getItem(i);
                if(item != null) {
                    final ItemMeta itemMeta = item.getItemMeta();
                    final List<String> lore = new ArrayList<>();
                    if(itemMeta.hasLore()) {
                        for(String s : itemMeta.getLore()) {
                            lore.add(s.replace("{PRICE}", p).replace("{ITEM}", it));
                        }
                    }
                    itemMeta.setLore(lore);
                    item.setItemMeta(itemMeta);
                }
            }
            top.setItem(top.firstEmpty(), its);
            player.updateInventory();
            purchasing.put(player, a);
        }
    }

    public List<AuctionedItemObj> getCollectionBin(@NotNull Player player) {
        return auctions.getOrDefault(player.getUniqueId(), new ArrayList<>());
    }
    public ItemStack getPlayerCollectionBin(@NotNull Player player) {
        final String size = Integer.toString(getCollectionBin(player).size());
        final ItemStack item = collectionBin.clone();
        final ItemMeta itemMeta = item.getItemMeta();
        final List<String> lore = new ArrayList<>();
        for(String s : itemMeta.getLore()) {
            lore.add(s.replace("{ITEMS}", size));
        }
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public AuctionedItemObj valueOf(@NotNull Player player, int slot, String type) {
        final String T = type;
        type = type.toUpperCase();
        final int page = this.page.getOrDefault(player, 0), p = (page-1)*slots.size();
        switch (type) {
            case "COLLECTION_BIN":
                final List<AuctionedItemObj> bin = getCollectionBin(player);
                return bin.size() > p+slot ? (AuctionedItemObj) bin.toArray()[p+slot] : null;
            case "AUCTION_HOUSE":
                final Collection<AuctionedItemObj> ah = auctionHouse.values();
                return ah.size() > p+slot ? (AuctionedItemObj) ah.toArray()[p+slot] : null;
            default:
                final UMaterial u = viewingCategory.get(player);
                final String a = "CATEGORY_" + u.name() + "_";
                final String[] b = T.split(a);
                String s = b.length == 1 ? null : b[1];
                final List<AuctionedItemObj> i = category.get(u).get(s);
                return slot < i.size() ? i.get(slot) : null;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        final String t = event.getView().getTitle();
        final boolean isAH = t.equals(ah.getTitle()), isC = t.equals(categories.getTitle()), isCB = t.equals(collectionbin.getTitle()), isCA = t.equals(confirmAuction.getTitle()), isP = purchasing.containsKey(player), isCV = t.equals(categoryItems.getTitle());
        if(isAH || isC || isCB || isCA || isP || isCV) {
            event.setCancelled(true);
            player.updateInventory();
            final ItemStack c = event.getCurrentItem();
            final int r = event.getRawSlot();
            if(r < 0 || r >= top.getSize() || c == null || c.getType().equals(Material.AIR)) return;

            final UUID u = player.getUniqueId();
            final String click = event.getClick().name();

            if(isAH || isC || isCB || isCV) {
                final boolean next = r == nextPageSlot, previous = r == previousPageSlot;
                if(next || previous) {
                    if(next) nextPage(player);
                    else previousPage(player);
                    return;
                } else if(c.equals(getPlayerCollectionBin(player))) {
                    player.closeInventory();
                    viewCollectionBin(player);
                    return;
                } else if(c.equals(refresh)) {
                    updatePage(player);
                    return;
                } else if(c.equals(returnToAH)) {
                    player.closeInventory();
                    view(player, 1);
                    return;
                } else if(c.equals(mainCategoryView)) {
                    player.closeInventory();
                    viewCategories(player);
                    return;
                }
            }

            if(isP) {
                final AuctionedItemObj ai = purchasing.get(player);
                final ItemStack z = ai != null ? ai.getItem() : null;
                final UUID auctioner = ai != null ? ai.getAuctioner() : null;
                final OfflinePlayer OP = ai != null ? Bukkit.getOfflinePlayer(auctioner) : null;
                final HashMap<String, String> replacements = new HashMap<>();
                final BigDecimal price = ai != null ? ai.getPrice() : BigDecimal.ZERO;
                final double priceDouble = price.doubleValue();
                final String p = formatBigDecimal(price);
                replacements.put("{PRICE}", p);
                replacements.put("{ITEM}", z != null ? z.hasItemMeta() && z.getItemMeta().hasDisplayName() ? z.getItemMeta().getDisplayName() : toMaterial(UMaterial.match(z).name(), false) : "");
                replacements.put("{PURCHASER}", player.getName());
                replacements.put("{SELLER}", ai != null ? OP.getName() : "");
                if(confirmPurchaseSlots.contains(r)) {
                    purchasing.remove(player);
                    if(ai == null) {
                        sendStringListMessage(player, getStringList(config, "messages.item no longer exists"), replacements);
                    } else if(auctioner.equals(u)) {
                        player.closeInventory();
                        sendStringListMessage(player, getStringList(config, "messages.cannot purchase own item"), replacements);
                        view(player, 1);
                        return;
                    } else if(ECONOMY.withdrawPlayer(player, priceDouble).transactionSuccess()) {
                        sendStringListMessage(player, getStringList(config, "messages.purchased auction"), replacements);
                        giveItem(player, z);
                        auctionHouse.remove(ai.getAuctionedTime());
                        auctions.get(auctioner).remove(ai);
                        if(OP.isOnline()) {
                            sendStringListMessage(OP.getPlayer(), getStringList(config, "messages.sold auction"), replacements);
                        }
                        ECONOMY.depositPlayer(OP, priceDouble);
                    } else {
                        sendStringListMessage(player, getStringList(config, "messages.cannot afford"), replacements);
                    }
                } else if(cancelPurchaseSlots.contains(r)) {
                } else return;
                player.closeInventory();
            } else if(isAH) {
                if(slots.contains(r)) {
                    final AuctionedItemObj target = valueOf(player, r, "AUCTION_HOUSE");
                    if(target != null) {
                        if(target.getAuctioner().equals(u) && click.equals("SHIFT_RIGHT")) {
                            cancelAuction(player, target);
                            updatePage(player);
                        } else {
                            tryPurchasing(player, target);
                        }
                    }
                } else if(c.equals(categoryView)) {
                    player.closeInventory();
                    viewCategories(player);
                }
            } else if(isC) {
                player.closeInventory();
                viewCategory(player, UMaterial.match(c), c.getItemMeta().hasDisplayName() ? c.getItemMeta().getDisplayName() : null);
            } else if(isCA) {
                final HashMap<ItemStack, BigDecimal> i = auctioning.get(player);
                final ItemStack it = (ItemStack) i.keySet().toArray()[0];
                final BigDecimal price = i.get(it);
                if(confirmAuctionSlots.contains(r)) {
                    auction(player, it, price);
                    auctioning.remove(player);
                } else if(!cancelAuctionSlots.contains(r)) {
                    return;
                }
                player.closeInventory();
                player.updateInventory();
            } else if(isCV) {
                if(slots.contains(r)) {
                    final UMaterial um = UMaterial.match(c);
                    final AuctionedItemObj a = valueOf(player, slots.indexOf(r), "CATEGORY_" + um.name() + (c.getItemMeta().hasDisplayName() ? "_" + c.getItemMeta().getDisplayName() : ""));
                    if(a != null) {
                        tryPurchasing(player, a);
                    }
                }
            } else { // Collection Bin
                if(slots.contains(r)) {
                    expire(player, valueOf(player, r, "COLLECTION_BIN"));
                }
            }
        }
    }

    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        viewing.remove(player);
        viewingCategory.remove(player);
        page.remove(player);
        if(auctioning != null && auctioning.containsKey(player)) {
            giveItem(player, (ItemStack) auctioning.get(player).keySet().toArray()[0]);
            auctioning.remove(player);
            sendStringListMessage(player, getStringList(config, "messages.auction sell cancelled"), null);
            player.updateInventory();
        } else if(purchasing != null && purchasing.containsKey(player)) {
            purchasing.remove(player);
            sendStringListMessage(player, getStringList(config, "messages.auction purchase cancelled"), null);
        }
    }
}
