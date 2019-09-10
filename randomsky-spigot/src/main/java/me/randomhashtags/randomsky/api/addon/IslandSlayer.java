package me.randomhashtags.randomsky.api.addon;

import me.randomhashtags.randomsky.addon.island.Island;
import me.randomhashtags.randomsky.api.IslandAddon;
import me.randomhashtags.randomsky.util.universal.UInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public class IslandSlayer extends IslandAddon {
    private static IslandSlayer instance;
    public static IslandSlayer getIslandSlayer() {
        if(instance == null) instance = new IslandSlayer();
        return instance;
    }

    public YamlConfiguration slayerConfig;

    private UInventory gui;
    private ItemStack background;
    private String unlockedName, lockedName;
    private List<String> progression, unlockedLore, lockedLore, respawnRate;

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "island slayer.yml");
        slayerConfig = YamlConfiguration.loadConfiguration(new File(randomsky.getDataFolder(), "island slayer.yml"));

        final int size = slayerConfig.getInt("gui.size");
        final List<String> format = colorizeListString(slayerConfig.getStringList("gui.settings.format"));
        progression = colorizeListString(slayerConfig.getStringList("gui.settings.progression"));
        unlockedLore = colorizeListString(slayerConfig.getStringList("gui.settings.unlocked.lore"));
        lockedLore = colorizeListString(slayerConfig.getStringList("gui.settings.locked.lore"));
        respawnRate = colorizeListString(slayerConfig.getStringList("gui.settings.respawn rate"));
        unlockedName = ChatColor.translateAlternateColorCodes('&', slayerConfig.getString("gui.settings.unlocked.name"));
        lockedName = ChatColor.translateAlternateColorCodes('&', slayerConfig.getString("gui.settings.locked.name"));
        gui = new UInventory(null, size, ChatColor.translateAlternateColorCodes('&', slayerConfig.getString("gui.title")));
        final Inventory gi = gui.getInventory();
        background = d(slayerConfig, "gui.background");
        int level = 1;
        for(String s : slayerConfig.getConfigurationSection("mobs").getKeys(false)) {
            final String p = "mobs." + s + ".", required = slayerConfig.getString(p + "required");
            final int slot = slayerConfig.getInt(p + "slot");
            final ItemStack display = d(slayerConfig, "mobs." + s);
            new SlayerSkill(s, level, slot, slayerConfig.getString(p + "entity").toUpperCase(), slayerConfig.getInt(p + "completion"), ChatColor.translateAlternateColorCodes('&', slayerConfig.getString(p + "slayer {TYPE}")), display, SlayerSkill.valueOf(required));
            item = display.clone(); itemMeta = item.getItemMeta(); lore.clear();
            if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
            lore.addAll(format);
            itemMeta.setLore(lore); lore.clear();
            item.setItemMeta(itemMeta);
            gi.setItem(slot, item);
            level++;
        }
        for(int i = 0; i < size; i++) {
            if(gi.getItem(i) == null) {
                gi.setItem(i, background);
            }
        }
        sendConsoleMessage("&6[RandomSky] &aLoaded " + (level-1) + " Slayer Skills &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        SlayerSkill.paths = null;
        SlayerSkill.slots = null;
    }

    public void viewSlayer(Player player) {
        if(hasPermission(player, "RandomSky.island.slayer", true)) {
            final Island island = Island.players.getOrDefault(player.getUniqueId(), null);
            if(island == null) {
                sendStringListMessage(player, config.getStringList("messages.need island"), null);
            } else {
                player.closeInventory();
                final int size = gui.getSize();
                player.openInventory(Bukkit.createInventory(player, size, gui.getTitle()));
                final Inventory top = player.getOpenInventory().getTopInventory();
                final HashMap<String, Integer> slainMobs = island.slainMobs;
                final List<String> allowedMobs = island.allowedMobs;
                top.setContents(gui.getInventory().getContents());
                for(int i = 0; i < size; i++) {
                    final SlayerSkill sk = SlayerSkill.slots.getOrDefault(i, null);
                    if(sk != null) {
                        final SlayerSkill r = sk.required;
                        final boolean isUnlocked = allowedMobs.contains(sk.entity);
                        final List<String> status = isUnlocked ? unlockedLore : lockedLore;
                        final double slainR = r != null ? slainMobs.getOrDefault(r.entity, 0) : 0.00, c = sk.completion, cpR = c != 0.00 ? (slainR/c)*100 : 0;
                        final String rr = formatDouble(island.mobRespawnRate.getOrDefault(sk.entity, 1.00)*100), rTYPE = r != null ? r.type : null, progress = formatDouble(slainMobs.getOrDefault(sk.entity, 0)), progressR = formatDouble(slainR), completion = Integer.toString((int) c), completionP = Integer.toString((int) cpR), TYPE = sk.type;
                        item = top.getItem(i).clone();
                        itemMeta = item.getItemMeta(); lore.clear();
                        itemMeta.setDisplayName((isUnlocked ? unlockedName : lockedName).replace("{NAME}", itemMeta.getDisplayName()));
                        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        for(String s : itemMeta.getLore()) {
                            if(s.equals("{PROGRESS}")) {
                                if(r != null) {
                                    for(String p : progression) {
                                        lore.add(p.replace("{TYPE}", rTYPE).replace("{PROGRESS}", progressR).replace("{COMPLETION%}", completionP).replace("{COMPLETION}", completion));
                                    }
                                }
                            } else if(s.equals("{RESPAWN_RATE}")) {
                                for(String p : respawnRate) {
                                    lore.add(p.replace("{PROGRESS}", progress).replace("{TYPE}", TYPE).replace("{RESPAWN%}", rr));
                                }
                            } else if(s.equals("{STATUS}")) {
                                lore.addAll(status);
                            } else {
                                lore.add(s);
                            }
                        }
                        itemMeta.setLore(lore); lore.clear();
                        item.setItemMeta(itemMeta);
                        if(isUnlocked) item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
                        top.setItem(i, item);
                    }
                }
                player.updateInventory();
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        if(event.getView().getTitle().equals(gui.getTitle())) {
            event.setCancelled(true);
            player.updateInventory();
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityDeathEvent(EntityDeathEvent event) {
        final LivingEntity e = event.getEntity();
        final String entity = getEntityType(e);
        final Island is = Island.valueOf(e.getLocation());
        if(is != null && entity != null) {
            final HashMap<String, BigDecimal> slain = is.getSlainMobs();
            if(slain.containsKey(entity)) {
                slain.put(entity, slain.get(entity).add(BigDecimal.ONE));
            }
        }
    }
}
