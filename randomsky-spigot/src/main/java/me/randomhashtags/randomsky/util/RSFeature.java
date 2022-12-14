package me.randomhashtags.randomsky.util;

import me.randomhashtags.randomsky.addon.enchant.CustomEnchant;
import me.randomhashtags.randomsky.addon.util.Loadable;
import me.randomhashtags.randomsky.api.unfinished.CustomEnchants;
import me.randomhashtags.randomsky.universal.UInventory;
import me.randomhashtags.randomsky.universal.UMaterial;
import me.randomhashtags.randomsky.universal.UVersion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import static java.io.File.separator;

public abstract class RSFeature extends UVersion implements Listener, Loadable, Mathable {
    private boolean isEnabled = false;
    private static boolean mcmmoIsEnabled = false;
    public boolean isEnabled() { return isEnabled; }
    protected boolean mcmmoIsEnabled() { return mcmmoIsEnabled; }

    private static final TreeMap<Integer, String> treemap = new TreeMap<>();

    public static YamlConfiguration otherdata;
    protected static File otherdataF;
    public static UInventory givedp;
    public static List<Inventory> givedpCategories;

    public void enable() {
        if(otherdataF == null) {
            save("_Data", "other.yml");
            otherdataF = new File(DATA_FOLDER + separator + "_Data", "other.yml");
            otherdata = YamlConfiguration.loadConfiguration(otherdataF);

            treemap.put(1000, "M"); treemap.put(900, "CM"); treemap.put(500, "D"); treemap.put(400, "CD"); treemap.put(100, "C"); treemap.put(90, "XC");
            treemap.put(50, "L"); treemap.put(40, "XL"); treemap.put(10, "X"); treemap.put(9, "IX"); treemap.put(5, "V"); treemap.put(4, "IV"); treemap.put(1, "I");

            givedp = new UInventory(null, 27, "Givedp Categories");
            givedpCategories = new ArrayList<>();

            mcmmoIsEnabled = PLUGIN_MANAGER.isPluginEnabled("mcMMO");
        }
        if(isEnabled) return;
        try {
            isEnabled = true;
            load();
            PLUGIN_MANAGER.registerEvents(this, RANDOM_SKY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void disable() {
        if(!isEnabled) return;
        try {
            isEnabled = false;
            unload();
            HandlerList.unregisterAll(this);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void saveOtherData() {
        try {
            otherdata.save(otherdataF);
            otherdataF = new File(DATA_FOLDER + separator + "_Data", "other.yml");;
            otherdata = YamlConfiguration.loadConfiguration(otherdataF);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void viewGivedp(Player player) {
        player.openInventory(Bukkit.createInventory(player, givedp.getSize(), givedp.getTitle()));
        player.getOpenInventory().getTopInventory().setContents(givedp.getInventory().getContents());
        player.updateInventory();
    }
    public void addGivedpCategory(List<ItemStack> items, UMaterial m, String what, String invtitle) {
        item = m.getItemStack(); itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + what);
        item.setItemMeta(itemMeta);
        givedp.getInventory().addItem(item);
        final int size = items.size();
        final Inventory inv = Bukkit.createInventory(null, size == 9 || size == 18 || size == 27 || size == 36 || size == 45 || size == 54 ? size : ((size+9)/9)*9, invtitle);
        for(ItemStack is : items) if(is != null) inv.addItem(is);
        givedpCategories.add(inv);
    }
    public String toRoman(int number) {
        // This code is from "bhlangonijr" at https://stackoverflow.com/questions/12967896
        if(number <= 0) return "";
        int l = treemap.floorKey(number);
        if(number == l) return treemap.get(number);
        return treemap.get(l) + toRoman(number - l);
    }
    public boolean hasPermission(CommandSender sender, String permission, boolean sendNoPermMessage) {
        if(!(sender instanceof Player) || sender.hasPermission(permission)) return true;
        else if(sendNoPermMessage) {
            sendStringListMessage(sender, RANDOM_SKY.getConfig().getStringList("no permission"), null);
        }
        return false;
    }


    public ItemStack d(FileConfiguration config, String path) {
        return d(config, path, 0.00);
    }
    public ItemStack d(FileConfiguration config, String path, double enchantMultiplier) {
        item = null;
        if(config == null && path != null || config != null && config.get(path + ".item") != null) {
            final String PP = config == null ? path : config.getString(path + ".item");
            String P = PP.toLowerCase();

            int amount = config != null && config.get(path + ".amount") != null ? config.getInt(path + ".amount") : 1;
            if(P.toLowerCase().contains(";amount=")) {
                final String A = P.split("=")[1];
                final boolean B = P.contains("-");
                final int min = B ? Integer.parseInt(A.split("-")[0]) : 0;
                amount = B ? min+ RANDOM.nextInt(Integer.parseInt(A.split("-")[1])-min+1) : Integer.parseInt(A);
                path = path.split(";amount=")[0];
                P = P.split(";")[0];
            }
            if(P.contains("spawner") && !P.startsWith("mob_spawner") && !path.equals("mysterymobspawner")) {
                return getSpawner(P);
            } else if(P.startsWith("enchantedbook:")) {
                final Enchantment e = getEnchantment(P.split(":")[1]);
                if(e != null) {
                    int level = 1;
                    if(P.split(":").length == 3)
                        level = P.split(":")[2].equals("random") ? 1 + RANDOM.nextInt(e.getMaxLevel()) : P.split(":")[2].contains("-") ? Integer.parseInt(P.split(":")[2].split("\\-")[0]) + RANDOM.nextInt(Integer.parseInt(P.split(":")[2].split("\\-")[1])) : Integer.parseInt(P.split(":")[2]);
                    item = new ItemStack(Material.ENCHANTED_BOOK, amount);
                    final EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                    meta.addStoredEnchant(e, level, true);
                    item.setItemMeta(meta);
                    return item;
                }
                return null;
            }
            ItemStack B = givedpitem.valueOf(PP);
            if(B == null) B = givedpitem.valueOf(P);
            if(B != null) {
                item = B.clone();
                item.setAmount(amount);
                return item;
            }
            boolean enchanted = config != null && config.getBoolean(path + ".enchanted");
            lore.clear();
            String name = config != null ? config.getString(path + ".name") : null;
            final String[] material = P.toUpperCase().split(":");
            final String mat = material[0];
            final byte data = material.length == 2 ? Byte.parseByte(material[1]) : 0;
            final UMaterial U = UMaterial.match(mat + (data != 0 ? ":" + data : ""));
            try {
                item = U.getItemStack();
            } catch(Exception e) {
                System.out.println("UMaterial null itemstack. mat=" + mat + ";data=" + data + ";versionName=" + (U != null ? U.getVersionName() : null) + ";getMaterial()=" + (U != null ? U.getMaterial() : null));
                return null;
            }
            final Material skullitem = UMaterial.PLAYER_HEAD_ITEM.getMaterial(), i = item.getType();
            if(!i.equals(Material.AIR)) {
                item.setAmount(amount);
                itemMeta = item.getItemMeta();
                if(i.equals(skullitem)) {
                    final String owner = P.contains(";owner=") ? P.split("=")[1].split("}")[0].split(";")[0] : "RandomHashTags";
                    final SkullMeta m = (SkullMeta) itemMeta;
                    m.setOwner(owner);
                    itemMeta = m;
                }
                itemMeta.setDisplayName(name != null ? ChatColor.translateAlternateColorCodes('&', name) : null);

                if(enchanted) itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                final HashMap<Enchantment, Integer> enchants = new HashMap<>();
                final CustomEnchants ce = CustomEnchants.getCustomEnchants();
                final boolean levelzeroremoval = ce.levelZeroRemoval;
                if(config != null && config.get(path + ".lore") != null) {
                    lore.clear();
                    for(String string : config.getStringList(path + ".lore")) {
                        final String sl = string.toLowerCase();
                        if(sl.startsWith("venchants{")) {
                            for(String s : string.split("\\{")[1].split("}")[0].split(";")) {
                                enchants.put(getEnchantment(s), getRemainingInt(s));
                            }
                        } else if(sl.startsWith("rpenchants{")) {
                            for(String s : string.split("\\{")[1].split("}")[0].split(";")) {
                                final CustomEnchant e = CustomEnchant.valueOf(s);
                                if(e != null && e.isEnabled()) {
                                    final EnchantRarity r = EnchantRarity.valueOf(e);
                                    if(r != null) {
                                        int l = getRemainingInt(s), x = (int) (e.getMaxLevel()*enchantMultiplier);
                                        l = l != -1 ? l : x+ RANDOM.nextInt(e.getMaxLevel()-x+1);
                                        if(l != 0 || !levelzeroremoval)
                                            lore.add(r.getApplyColors() + e.getName() + " " + toRoman(l != 0 ? l : 1));
                                    } else {
                                        System.out.println("[RandomPackage] WARNING: No CustomEnchantRarity found for enchant \"" + e.getName() + "\"!");
                                    }
                                }
                            }
                        } else {
                            lore.add(ChatColor.translateAlternateColorCodes('&', string));
                        }
                    }
                }
                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);
                lore.clear();
                if(enchanted) item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
                for(Enchantment enchantment : enchants.keySet()) {
                    if(enchantment != null) {
                        item.addUnsafeEnchantment(enchantment, enchants.get(enchantment));
                    }
                }
                if(name != null && name.contains("{ENCHANT_SIZE}")) ce.applyTransmogScroll(item);
            }
        }
        return item;
    }
}
