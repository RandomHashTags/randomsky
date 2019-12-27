package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.addon.PlayerSkill;
import me.randomhashtags.randomsky.addon.PlayerSkillLevel;
import me.randomhashtags.randomsky.addon.file.FilePlayerSkill;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.util.RSPlayer;
import me.randomhashtags.randomsky.util.RSStorage;
import me.randomhashtags.randomsky.universal.UInventory;
import org.bukkit.Bukkit;
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
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;

import static java.io.File.separator;

public class PlayerSkills extends RSFeature implements CommandExecutor {
    private static PlayerSkills instance;
    public static PlayerSkills getPlayerSkills() {
        if(instance == null) instance = new PlayerSkills();
        return instance;
    }

    public YamlConfiguration config;

    private UInventory gui;
    private ItemStack background, back;
    private ItemStack token, shard;

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
        save(DATA_FOLDER + separator + "player skills", "_settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER + separator + "player skills", "_settings.yml"));

        skills = new HashMap<>();
        viewing = new HashMap<>();

        gui = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        final Inventory gi = gui.getInventory();
        background = d(config, "gui.background");
        back = d(config, "gui.back");
        token = d(config, "items.token");
        shard = d(config, "items.shard");

        for(File f : new File(DATA_FOLDER + separator + "player skills").listFiles()) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                final FilePlayerSkill skill = new FilePlayerSkill(f);
            }
        }

        for(int i = 0; i < gui.getSize(); i++) {
            item = gi.getItem(i);
            if(item == null) gi.setItem(i, background);
        }
        sendConsoleMessage("&6[RandomSky] &aLoaded " + RSStorage.getAll(Feature.PLAYER_SKILL).size() + " Player Skills &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        for(Player p : viewing.keySet()) {
            p.closeInventory();
        }
        RSStorage.unregisterAll(Feature.PLAYER_SKILL, Feature.PLAYER_SKILL_LEVEL);
    }

    public void viewSkills(Player player) {
        if(hasPermission(player, "RandomSky.skills.view", true)) {
            player.closeInventory();
            final RSPlayer pdata = RSPlayer.get(player.getUniqueId());
            final int size = gui.getSize();
            player.openInventory(Bukkit.createInventory(player, size, gui.getTitle().replace("{TOKENS}", Integer.toString(pdata.getSkillTokens()))));
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
        if(hasPermission(player, "RandomSky.skills.view." + skill.getIdentifier(), true)) {
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
            pdata.setSkillTokens(pdata.getSkillTokens()+1);
            removeItem(player, i, 1);
            player.updateInventory();
        }
    }
}
