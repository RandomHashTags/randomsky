package me.randomhashtags.randomsky.addon.file;

import me.randomhashtags.randomsky.addon.ShopCategory;
import me.randomhashtags.randomsky.addon.ShopItem;
import me.randomhashtags.randomsky.addon.obj.ShopItemObj;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSAddon;
import me.randomhashtags.randomsky.util.newRSStorage;
import me.randomhashtags.randomsky.util.universal.UInventory;
import me.randomhashtags.randomsky.util.universal.UVersionable;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;

public class FileShopCategory extends RSAddon implements ShopCategory, UVersionable {
    private ItemStack display;
    private UInventory inv;
    private int slot;
    private HashMap<Integer, ShopItem> items;

    public FileShopCategory(File f, ItemStack display, int slot) {
        load(f);
        this.display = display;
        this.slot = slot;
        newRSStorage.register(Feature.SHOP_CATEGORY, this);
    }

    public String getIdentifier() { return getYamlName(); }
    public int getSlot() { return slot; }
    public ItemStack getItem() { return getClone(display); }
    public UInventory getInventory() {
        if(inv == null) {
            inv = new UInventory(null, yml.getInt("size"), colorize(yml.getString("title")));
            getItems();
        }
        return inv;
    }
    public HashMap<Integer, ShopItem> getItems() {
        if(items == null) {
            items = new HashMap<>();
            final UInventory uinv = getInventory();
            final Inventory inv = uinv.getInventory();
            for(String key : yml.getConfigurationSection("gui").getKeys(false)) {
                final String p = "gui." + key + ".";
                final int slot = yml.getInt(p + "slot");
                final String[] prices = yml.getString(p + "prices").split(";");
                final BigDecimal buyPrice = valueOfBigDecimal(prices[0]), sellPrice = valueOfBigDecimal(prices[1]);
                final ShopItemObj i = new ShopItemObj(key, slot, buyPrice, sellPrice);
                items.put(slot, i);

                inv.setItem(slot, null); // TODO
            }
        }
        return items;
    }
}
