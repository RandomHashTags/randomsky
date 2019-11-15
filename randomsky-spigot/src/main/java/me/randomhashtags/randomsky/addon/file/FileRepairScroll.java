package me.randomhashtags.randomsky.addon.file;

import com.sun.istack.internal.Nullable;
import me.randomhashtags.randomsky.addon.RepairScroll;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSAddon;
import me.randomhashtags.randomsky.util.RSStorage;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileRepairScroll extends RSAddon implements RepairScroll {
    private ItemStack item;
    private int percentslot = -1;
    private List<String> appliesto;

    public FileRepairScroll(File f) {
        load(f);
        RSStorage.register(Feature.REPAIR_SCROLL, this);
    }
    public String getIdentifier() { return getYamlName(); }

    public List<String> getAppliesTo() {
        if(appliesto == null) {
            final List<String> a = new ArrayList<>();
            for(String s : yml.getStringList("applies to")) {
                a.add(s.toUpperCase());
            }
            appliesto = a;
        }
        return appliesto;
    }
    public int getPercentSlot() {
        if(percentslot == -1) {
            final ItemMeta im = getItem().getItemMeta();
            final List<String> lore = im.getLore();
            for(int i = 0; i < lore.size(); i++) {
                if(lore.get(i).contains("{PERCENT}")) {
                    percentslot = i;
                }
            }
        }
        return percentslot;
    }
    public int getPercent(ItemStack is) { return getRemainingInt(getItem().getItemMeta().getLore().get(getPercentSlot())); }

    public ItemStack getItem() {
        if(item == null) item = api.d(yml, "item");
        return getClone(item);
    }
    public ItemStack getItem(int percent) {
        final String p = Integer.toString(percent);
        final ItemStack is = getItem();
        final ItemMeta m = is.getItemMeta();
        final List<String> lore = m.getLore(), l = new ArrayList<>();
        for(String s : lore) {
            l.add(s.replace("{PERCENT}", p));
        }
        m.setLore(l);
        is.setItemMeta(m);
        return is;
    }

    public boolean canBeApplied(@Nullable ItemStack is) {
        if(is != null) {
            final String m = is.getType().name();
            for(String s : getAppliesTo()) {
                if(m.endsWith(s)) {
                    return true;
                }
            }
        }
        return false;
    }
}
