package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.addon.PlayerSkill;
import me.randomhashtags.randomsky.addon.PlayerSkillLevel;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSPlayer;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class PlayerSkills extends RSFeature implements CommandExecutor {
    private static PlayerSkills instance;
    public static PlayerSkills getPlayerSkills() {
        if(instance == null) instance = new PlayerSkills();
        return instance;
    }

    public YamlConfiguration config;

    private UInventory gui;
    private ItemStack background, back;
    public ItemStack token, shard;

    private HashMap<PlayerSkill, UInventory> skills;
    private HashMap<Player, PlayerSkill> viewing;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final int l = args.length;
        if(l == 0) {
            if(player != null) {
                viewSkills(player);
            }
        } else {
            final String a = args[0];
            if(a.equals("help")) {
                viewHelp(sender);
            }
        }
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "player skills.yml");
        config = YamlConfiguration.loadConfiguration(new File(randomsky.getDataFolder(), "player skills.yml"));

        skills = new HashMap<>();
        viewing = new HashMap<>();

        gui = new UInventory(null, config.getInt("gui.size"), ChatColor.translateAlternateColorCodes('&', config.getString("gui.title")));
        final Inventory gi = gui.getInventory();
        background = d(config, "gui.background");
        back = d(config, "gui.back");
        token = d(config, "items.token");
        shard = d(config, "items.shard");
        int loaded = 0;
        for(String s : config.getConfigurationSection("skills").getKeys(false)) {
            if(!s.equals("settings")) {
                final String p = "skills." + s + ".";
                final int slot = config.getInt(p + "slot");
                final ItemStack display = d(config, "skills." + s);
                final PlayerSkill ps = new PlayerSkill(s, config.getInt(p + "max level"), slot, display);
                final List<String> levelFormat = colorizeListString(config.getStringList(p + "level format"));
                loaded++;
                final ItemStack k = display.clone(); itemMeta = item.getItemMeta(); lore.clear();
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                lore.addAll(levelFormat);
                k.setItemMeta(itemMeta); lore.clear();

                UInventory i = new UInventory(null, 54, ChatColor.translateAlternateColorCodes('&', config.getString(p + "title")));
                final Inventory ii = i.getInventory();
                int level = 1, highestSlot = 0;
                for(String parent : config.getConfigurationSection("skills." + s).getKeys(false)) {
                    if(!parent.equals("type") && !parent.equals("title") && !parent.equals("max level") && !parent.equals("slot") && !parent.equals("level format") && !parent.equals("item") && !parent.equals("name") && !parent.equals("lore")) {
                        final String O = p + parent + ".", it = config.getString(O + "item");
                        final int slott = config.getInt(O + "slot");
                        highestSlot = slott > highestSlot ? slott : highestSlot;
                        final ItemStack d = it != null ? it.toLowerCase().equals("back") ? back : d(config, p + "." + parent) : new ItemStack(Material.AIR);
                        if(d != back) {
                            itemMeta = d.getItemMeta();
                            itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
                            d.setItemMeta(itemMeta);
                            ps.levels.add(new PlayerSkillLevel(ps, parent, level, slott, d, ChatColor.translateAlternateColorCodes('&', config.getString(O + "perk")), config.getStringList(O + "attributes")));
                            level++;
                        }
                        ii.setItem(slott, d);
                    }
                }
                final ItemStack[] con = ii.getContents().clone();
                i = new UInventory(null, highestSlot%9 == 0 ? highestSlot : ((highestSlot+9)/9)*9, i.getTitle());
                for(int z = 0; z < con.length && z < i.getSize(); z++) {
                    final ItemStack target = con[z];
                    ii.setItem(z, target != null ? target : background);
                }

                item = k.clone(); itemMeta = item.getItemMeta();
                for(String m : itemMeta.getLore()) {
                    if(m.equals("{LEVELS}")) {
                        for(PlayerSkillLevel l : ps.levels) {
                            final String L = Integer.toString(l.level), perk = l.perk;
                            for(String lvl : levelFormat) {
                                lore.add(lvl.replace("{LEVEL}", L).replace("{PERK}", perk));
                            }
                        }
                    } else {
                        lore.add(m);
                    }
                }
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
                gi.setItem(slot, item);
                skills.put(ps, i);
            }
        }

        for(int i = 0; i < gui.getSize(); i++) {
            item = gi.getItem(i);
            if(item == null) gi.setItem(i, background);
        }
        sendConsoleMessage("&6[RandomSky] &aLoaded " + loaded + " Player Skills &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        for(Player p : viewing.keySet()) {
            p.closeInventory();
        }
        playerskills = null;
    }

    public void viewSkills(Player player) {
        if(hasPermission(player, "RandomSky.skills.view", true)) {
            player.closeInventory();
            final RSPlayer pdata = RSPlayer.get(player.getUniqueId());
            final int size = gui.getSize();
            player.openInventory(Bukkit.createInventory(player, size, gui.getTitle().replace("{TOKENS}", Integer.toString(pdata.skillTokens))));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());
            viewing.put(player, null);
            for(int i = 0; i < size; i++) {
                final PlayerSkill s = PlayerSkill.slots.getOrDefault(i, null);
                if(s != null) {
                    final int L = pdata.getPlayerSkillLevel(s);
                    final String level = Integer.toString(L);
                    item = top.getItem(i); itemMeta = item.getItemMeta(); lore.clear();
                    int lvl = 1;
                    for(String l : itemMeta.getLore()) {
                        lore.add(l.replace("{LEVEL}", level).replace("{STATUS}", L >= lvl ? "" : ""));
                        if(l.contains("{LEVEL}")) lvl++;
                    }
                    itemMeta.setLore(lore); lore.clear();
                    item.setItemMeta(itemMeta);
                }
            }
            player.updateInventory();
        }
    }

    public void viewHelp(CommandSender sender) {
        if(hasPermission(sender, "RandomSky.skills.help", true)) {
            sendStringListMessage(sender, config.getStringList("messages.help"), null);
        }
    }
    public void viewSkillLevels(Player player, PlayerSkill skill) {
        if(hasPermission(player, "RandomSky.skills.view." + skill.path, true)) {
            player.closeInventory();
            final UInventory target = skills.get(skill);
            if(target != null) {
                player.openInventory(Bukkit.createInventory(player, target.getSize(), target.getTitle()));
                final Inventory top = player.getOpenInventory().getTopInventory();
                top.setContents(target.getInventory().getContents());
                player.updateInventory();
                viewing.put(player, skill);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        if(viewing.containsKey(player)) {
            event.setCancelled(true);
            player.updateInventory();
            final ItemStack c = event.getCurrentItem();
            final int r = event.getRawSlot();
            if(r < 0 || r >= player.getOpenInventory().getTopInventory().getSize() || c == null || c.getType().equals(Material.AIR)) return;

            final PlayerSkill v = viewing.get(player);
            if(v == null) {
                viewSkillLevels(player, PlayerSkill.slots.get(r));
            } else if(c.equals(back)) {
                player.closeInventory();
                viewSkills(player);
            } else {
                final PlayerSkillLevel level = PlayerSkillLevel.valueOf(v, r);
                if(level != null) {
                    Bukkit.broadcastMessage("PlayerSkills;level != null");
                }
            }
        }
    }
    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        viewing.remove(player);
    }
    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack i = event.getItem();
        if(i != null && i.isSimilar(token)) {
            event.setCancelled(true);
            final Player player = event.getPlayer();
            final RSPlayer pdata = RSPlayer.get(player.getUniqueId());
            pdata.skillTokens++;
            removeItem(player, i, 1);
            player.updateInventory();
        }
    }
}
