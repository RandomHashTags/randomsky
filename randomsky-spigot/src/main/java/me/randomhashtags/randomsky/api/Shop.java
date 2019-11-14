package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.addon.ShopCategory;
import me.randomhashtags.randomsky.addon.ShopItem;
import me.randomhashtags.randomsky.addon.file.FileShopCategory;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSPlayer;
import me.randomhashtags.randomsky.util.classes.island.Island;
import me.randomhashtags.randomsky.util.newRSStorage;
import me.randomhashtags.randomsky.util.universal.UInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static java.io.File.separator;

public class Shop extends RSFeature implements Listener, CommandExecutor {
    private static Shop instance;
    public static Shop getShop() {
        if(instance == null) instance = new Shop();
        return instance;
    }

    public YamlConfiguration config;
    private UInventory inv;
    private String originBonus;
    private HashMap<String, UInventory> inventories;

    private HashMap<String, ShopCategory> shopTitles;

    private HashMap<Integer, String> invCategories;
    public ItemStack back;

    private List<String> format, buy, buyusage, sell, sellusage, usagespacing;

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "shop.yml");
        config = YamlConfiguration.loadConfiguration(new File(dataFolder, "shop.yml"));

        shopTitles = new HashMap<>();

        inventories = new HashMap<>();
        invCategories = new HashMap<>();

        back = d(config, "categories.back");
        originBonus = colorize(config.getString("messages.origin bonus"));

        format = colorizeListString(config.getStringList("lores.format"));
        buy = colorizeListString(config.getStringList("lores.buy"));
        buyusage = colorizeListString(config.getStringList("lores.buy usage"));
        sell = colorizeListString(config.getStringList("lores.sell"));
        sellusage = colorizeListString(config.getStringList("lores.sell usage"));
        usagespacing = colorizeListString(config.getStringList("lores.usage spacing"));

        final List<String> addedlore = colorizeListString(config.getStringList("categories.added lore"));
        inv = new UInventory(null, config.getInt("categories.size"), colorize(config.getString("categories.title")));
        final Inventory ii = inv.getInventory();

        if(!otherdata.getBoolean("saved default shops")) {
            final String[] A = new String[] {"BLOCKS", "CLAY", "CONTAINERS", "FARMING", "FENCES", "FISHING", "FLOWERS", "FOOD", "GLASS", "MOB_DROPS", "PERMISSION_BLOCKS", "POTIONS", "REDSTONE", "RESOURCE_NODES", "SAPLINGS", "SCIENCE", "SLABS", "SPAWNERS", "UTILITY", "WOOL"};
            for(String s : A) {
                save("shops", s + ".yml");
            }
            otherdata.set("saved default shops", true);
            saveOtherData();
        }
        scheduler.runTaskAsynchronously(randomsky, () -> {
            for(String s : config.getConfigurationSection("categories").getKeys(false)) {
                if(!s.equals("title") && !s.equals("size") && !s.equals("added lore") && !s.equals("background") && !s.equals("back")) {
                    final String p = "categories." + s + ".", opens = config.getString(p + "opens");
                    final File f = new File(dataFolder + separator + "shops", opens + ".yml");
                    if(f.exists()) {
                        final int slot = config.getInt(p + "slot");
                        invCategories.put(slot, opens);
                        final ItemStack display = d(config, p.substring(0, p.length()-1));
                        itemMeta = display.getItemMeta(); lore.clear();
                        if(itemMeta.hasLore()) {
                            lore.addAll(itemMeta.getLore());
                        }
                        lore.addAll(addedlore);
                        itemMeta.setLore(lore); lore.clear();
                        display.setItemMeta(itemMeta);
                        ii.setItem(slot, display);
                        new FileShopCategory(f, display, slot);
                    } else {
                        sendConsoleMessage("&6[RandomSky] &cERROR: Missing shop yml \"&f" + opens + "&c\"!");
                    }
                }
            }
            sendConsoleMessage("&6[RandomSky] &aLoaded " + newRSStorage.getAll(Feature.SHOP_CATEGORY).size() + " Shop Categories &e(took " + (System.currentTimeMillis()-started) + "ms) [async]");
        });
        final ItemStack background = d(config, "categories.background");
        for(int i = 0; i < inv.getSize(); i++) {
            if(ii.getItem(i) == null) {
                ii.setItem(i, background);
            }
        }
    }
    public void createCategory(String path, String opens, YamlConfiguration yml) {
        //shopItems.put(path, new ArrayList<>());
        final UInventory i = new UInventory(null, yml.getInt("size"), colorize(yml.getString("title")));
        final Inventory ii = i.getInventory();
        for(String s : yml.getConfigurationSection("gui").getKeys(false)) {
            final String p = "gui." + s + ".";
            final int slot  = yml.getInt(p + "slot");
            final String P =  yml.getString(p + "prices");
            final String[] prices = P != null ? P.split(";") : new String[] { "0.00", "0.00"};
            final double buyprice = Double.parseDouble(prices[0]), sellprice = Double.parseDouble(prices[1]);
            final ItemStack display = yml.getString(p + ".item").toLowerCase().equals("back") ? back : d(yml, "gui." + s), purchased = d(yml, p + "custom");
            item = display.clone();
            if(!display.equals(back)) {
                itemMeta = display.getItemMeta(); lore.clear();
                if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
                final boolean buyUsage = buyprice > 0.00, sellUsage = sellprice > 0.00;
                final String BB = formatDouble(buyprice), SS = formatDouble(sellprice), stack = Integer.toString(display.getMaxStackSize());
                for(String e : format) {
                    if(e.equals("{PRICES}")) {
                        if(buyUsage) {
                            for(String B : buy) lore.add(B.replace("{BUY}", BB));
                        }
                        if(sellUsage) {
                            for(String S : sell) lore.add(S.replace("{SELL}", SS));
                        }
                    } else if(e.equals("{USAGES}")) {
                        if(buyUsage) {
                            for(String S : buyusage) lore.add(S.replace("{STACK}", stack));
                            lore.addAll(usagespacing);
                        }
                        if(sellUsage) {
                            for(String S : sellusage) lore.add(S.replace("{STACK}", stack));
                        }
                    } else {
                        lore.add(colorize(e));
                    }
                }
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
            }
            //final ShopItem si = new ShopItem(yml, opens, s, slot, buyprice, sellprice, display, purchased, null);
            if(slot < 54) {
                ii.setItem(slot, item);
            }
            //shopItems.get(path).add(si);
        }
        inventories.put(opens, i);
    }
    public void unload() {
        shopcategories = null;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        if(player != null && args.length == 0) {
            viewMenu(player);
        }
        return true;
    }

    public void viewMenu(Player player) {
        if(hasPermission(player, "RandomSky.shop.menu", true)) {
            player.closeInventory();
            player.openInventory(Bukkit.createInventory(player, inv.getSize(), inv.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(inv.getInventory().getContents());
            player.updateInventory();
        }
    }
    public void view(Player player, String shop) {
        if(inventories.containsKey(shop)) {
            player.closeInventory();
            final UInventory i = inventories.get(shop);
            player.openInventory(Bukkit.createInventory(player, i.getSize(), i.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(i.getInventory().getContents());
            player.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        final String t = event.getView().getTitle(), it = inv.getTitle();
        final ShopCategory shop = valueOfShopTitle(t);
        if(t.equals(it) || shop != null) {
            event.setCancelled(true);
            player.updateInventory();

            final ItemStack c = event.getCurrentItem();
            final int r = event.getRawSlot();
            if(r < 0 || r >= top.getSize() || c == null || c.getType().equals(Material.AIR)) return;

            if(t.equals(it) && invCategories.containsKey(r)) {
                view(player, invCategories.get(r));
            } else if(shop != null) {
                if(c.equals(back)) {
                    viewMenu(player);
                } else {
                    final ShopItem s = shop.getItems().get(r);
                    if(s != null) {
                        final String click = event.getClick().name();
                        if(click.contains("LEFT")) {
                            tryBuying(player, s, click.contains("SHIFT"));
                        } else if(click.contains("RIGHT")) {
                            trySelling(player, s, click.contains("SHIFT"));
                        }
                    }
                }
            }
        }
    }

    public void tryBuying(Player player, ShopItem s, boolean stack) {
        final double price = s.getBuyPrice().doubleValue();
        if(price > 0.00) {
            final ItemStack purchased = s.getPurchasedItem();
            final int amount = stack ? purchased.getMaxStackSize() : 1;
            final double total = round(price*amount, 2);
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{AMOUNT}", Integer.toString(amount));
            replacements.put("{BUY}", formatDouble(price));
            replacements.put("{ITEM}", toMaterial(purchased.getType().name(), false));
            replacements.put("{TOTAL}", formatDouble(total));
            if(eco.withdrawPlayer(player, total).transactionSuccess()) {
                for(int i = 1; i <= amount; i++) giveItem(player, purchased);
                sendStringListMessage(player, config.getStringList("messages.purchased"), replacements);
            } else {
                sendStringListMessage(player, config.getStringList("messages.not enough funds"), replacements);
            }
        }
    }
    public void trySelling(Player player, ShopItem s, boolean stack) {
        final double price = s.getSellPrice().doubleValue();
        if(price > 0.00) {
            final ItemStack selling = s.getSoldItem();
            final int inva = getAmount(player.getInventory(), selling), amount = stack ? inva > 64 ? 64 : inva : 1;
            final Island is = RSPlayer.get(player.getUniqueId()).getIsland();
            final double m = is != null ? is.sellPriceMultiplier : 1.00, total = round(price*amount*m, 2);
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{TOTAL}", formatDouble(total));
            replacements.put("{SELL}", formatDouble(price));
            replacements.put("{ITEM}", toMaterial(selling.getType().name(), false));
            replacements.put("{AMOUNT}", Integer.toString(amount));
            replacements.put("{BONUS}", m != 1.00 ? originBonus.replace("{ORIGIN}", is.getOrigin().string).replace("{PERCENT}", formatDouble(round((m-1)*100, 2))) : "");
            if(inva == 0) {
                sendStringListMessage(player, config.getStringList("messages.not enough to sell"), replacements);
            } else {
                eco.depositPlayer(player, total);
                removeItem(player, selling, amount);
                sendStringListMessage(player, config.getStringList("messages.sold"), replacements);
            }
        }
    }

    public ShopCategory valueOfShopTitle(String title) {
        return shopTitles.getOrDefault(title, null);
    }
}
