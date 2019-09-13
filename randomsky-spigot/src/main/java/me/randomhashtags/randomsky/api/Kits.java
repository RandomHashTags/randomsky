package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSPlayer;
import me.randomhashtags.randomsky.util.universal.UInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class Kits extends RSFeature implements Listener {
    private static Kits instance;
    public static Kits getKits() {
        if(instance == null) instance = new Kits();
        return instance;
    }

    public boolean isEnabled = false;
    public YamlConfiguration config;
    private UInventory gui, preview;
    private ItemStack back;
    private List<String> format, claim, previeW, claimable, claimed, locked;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final int l = args.length;
        if(l == 0 && player != null) {
            viewKits(player);
        } else {
            final String a = args[0];
            if(a.equals("reset") && l == 2) {
                tryResetting(sender, args[1]);
            } else if(player != null) {
                final Kit k = Kit.kits.getOrDefault(a, null);
                if(k == null) {
                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("{INPUT}", a);
                    sendStringListMessage(player, config.getStringList("messages.unknown kit"), replacements);
                } else {
                    giveKit(player, RSPlayer.get(player.getUniqueId()), k, true, true);
                }
            }
        }
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "kits.yml");
        config = YamlConfiguration.loadConfiguration(new File(randomsky.getDataFolder(), "kits.yml"));

        back = d(config, "back");
        format = colorizeListString(config.getStringList("lores.format"));
        claim = colorizeListString(config.getStringList("lores.claim"));
        previeW = colorizeListString(config.getStringList("lores.preview"));
        claimable = colorizeListString(config.getStringList("lores.claimable"));
        claimed = colorizeListString(config.getStringList("lores.claimed"));
        locked = colorizeListString(config.getStringList("lores.locked"));

        gui = new UInventory(null, config.getInt("gui.size"), ChatColor.translateAlternateColorCodes('&', config.getString("gui.title")));
        final Inventory gi = gui.getInventory();
        preview = new UInventory(null, 54, ChatColor.translateAlternateColorCodes('&', config.getString("preview.title")));

        if(!otherdata.getBoolean("saved default kits")) {
            final String[] a = new String[]{"RANK", "STARTER"};
            for(String s : a) save("kits", s + ".yml");
            otherdata.set("saved default kits", true);
            saveOtherData();
        }

        for(File f : new File(rsd + separator + "kits").listFiles()) {
            final Kit k = new Kit(f);
            item = k.getItem();
            itemMeta = item.getItemMeta(); lore.clear();
            for(String e : format) {
                if(e.equals("{KIT_LORE}")) {
                    if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
                } else {
                    lore.add(e);
                }
            }
            itemMeta.setLore(lore); lore.clear();
            item.setItemMeta(itemMeta);
            gi.setItem(k.getSlot(), item);
        }
        sendConsoleMessage("&6[RandomSky] &aLoaded " + (kits != null ? kits.size() : 0) + " Kits &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        kits = null;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final String t = event.getView().getTitle();
        if(t.equals(gui.getTitle()) || t.startsWith(preview.getTitle().replace("{KIT}", ""))) {
            final Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            player.updateInventory();
            final ItemStack c = event.getCurrentItem();
            final int r = event.getRawSlot();
            final Inventory top = player.getOpenInventory().getTopInventory();
            if(r < 0 || r >= top.getSize() || c == null || c.getType().equals(Material.AIR)) return;
            final Kit kit = t.equals(gui.getTitle()) ? Kit.valueOf(r) : null;
            if(kit != null) {
                final String click = event.getClick().name();
                if(click.contains("LEFT")) {
                    tryClaiming(player, RSPlayer.get(player.getUniqueId()), top, r, kit);
                } else if(click.contains("RIGHT")) {
                    tryPreviewing(player, kit);
                }
            } else if(c.equals(back)) {
                viewKits(player);
            }
        }
    }

    public void viewKits(Player player) {
        if(hasPermission(player, "RandomSky.kits.view", true)) {
            final RSPlayer pdata = RSPlayer.get(player.getUniqueId());
            player.closeInventory();
            final int size = gui.getSize();
            player.openInventory(Bukkit.createInventory(player, size, gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());

            for(int i = 0; i < size; i++) {
                final Kit k = Kit.valueOf(i);
                if(k != null) {
                    top.setItem(i, getStatus(player, top, i, pdata, k));
                }
            }
            player.updateInventory();
        }
    }
    private ItemStack getStatus(Player player, Inventory top, int slot, RSPlayer pdata, Kit kit) {
        final String n = kit.getYamlName();
        final boolean canClaim = canClaim(pdata, kit);
        final HashMap<Kit, Long> k = pdata.getKitExpirations();
        final String cooldown = getRemainingTime(kit.getCooldown()*1000), remainingTime = k.containsKey(kit) ? getRemainingTime(k.get(kit)-System.currentTimeMillis()) : "0s";
        item = top.getItem(slot);
        itemMeta = item.getItemMeta(); lore.clear();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
        for(String s : itemMeta.getLore()) {
            if(s.equals("{USAGE}")) {
                lore.addAll(claim);
                if(hasPermission(player, "RandomSky.kits." + n, false)) {
                    lore.addAll(previeW);
                }
            } else if(s.equals("{STATUS}")) {
                if(canClaim) {
                    lore.addAll(claimable);
                } else {
                    for(String l : claimed) {
                        lore.add(l.replace("{REMAINING_TIME}", remainingTime));
                    }
                }
            } else {
                lore.add(s.replace("{COOLDOWN}", cooldown));
            }
        }
        itemMeta.setLore(lore); lore.clear();
        item.setItemMeta(itemMeta);
        if(canClaim) item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        return item;
    }
    public boolean canClaim(RSPlayer pdata, Kit kit) {
        final HashMap<Kit, Long> e = pdata.getKitExpirations();
        return !e.containsKey(kit) || System.currentTimeMillis() >= e.get(kit);
    }
    public void tryClaiming(Player player, RSPlayer pdata, Inventory top, int slot, Kit kit) {
        if(hasPermission(player, "RandomSky.kits." + kit.getYamlName(), true)) {
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{KIT}", kit.getName());
            final HashMap<Kit, Long> k = pdata.getKitExpirations();

            if(k.containsKey(kit)) {
                final long timeLeft = k.get(kit)-System.currentTimeMillis();
                replacements.put("{REMAINING_TIME}", getRemainingTime(timeLeft));
                if(timeLeft <= 0) {
                    giveKit(player, pdata, kit, true, true);
                    player.closeInventory();
                } else {
                    sendStringListMessage(player, config.getStringList("messages.on cooldown"), replacements);
                }
            } else {
                giveKit(player, pdata, kit, true, true);
                player.closeInventory();
            }
            top.setItem(slot, getStatus(player, top, slot, pdata, kit));
            player.updateInventory();
        }
    }
    private void giveKit(Player player, RSPlayer pdata, Kit kit, boolean addCooldown, boolean sendMessage) {
        final List<ItemStack> items = kit.items();
        for(ItemStack is : items) {
            giveItem(player, is);
        }
        if(sendMessage) {
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{KIT}", kit.getName());
            sendStringListMessage(player, config.getStringList("messages.claimed"), replacements);
        }
        if(addCooldown) {
            pdata.getKitExpirations().put(kit, System.currentTimeMillis()+(kit.getCooldown()*1000));
        }
    }
    public void tryPreviewing(Player player, Kit kit) {
        player.closeInventory();
        final List<ItemStack> items = kit.items();
        final int s = items.size(), size = s%9 == 0 ? s : ((s+9)/9)*9;
        player.openInventory(Bukkit.createInventory(player, size, preview.getTitle().replace("{KIT}", kit.getName())));
        final Inventory top = player.getOpenInventory().getTopInventory();
        for(ItemStack is : items) top.setItem(top.firstEmpty(), is);
        top.setItem(top.getSize()-1, back);
        player.updateInventory();
    }
    public void tryResetting(CommandSender sender, String target) {
        if(hasPermission(sender, "RandomSky.kits.reset", true)) {
            final OfflinePlayer op = Bukkit.getOfflinePlayer(target);
            if(op != null) {
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{PLAYER}", op.getName());
                sendStringListMessage(sender, config.getStringList("messages.reset"), replacements);
                RSPlayer.get(op.getUniqueId()).getKitExpirations().clear();
            }
        }
    }
}