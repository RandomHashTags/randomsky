package me.randomhashtags.randomsky.api;

import me.randomhashtags.randomsky.util.RSFeature;
import me.randomhashtags.randomsky.universal.UInventory;
import me.randomhashtags.randomsky.universal.UMaterial;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;

import java.io.File;
import java.util.*;

public class CustomCrafting extends RSFeature implements CommandExecutor {
    private static CustomCrafting instance;
    public static CustomCrafting getCustomCrafting() {
        if(instance == null) instance = new CustomCrafting();
        return instance;
    }
    public YamlConfiguration config;

    private UInventory gui;
    private ItemStack background;
    private List<String> removeRecipes;

    protected HashMap<CustomRecipe, ShapedRecipe> recipes;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) return true;
        final Player player = (Player) sender;
        viewCrafting(player);
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "custom crafting/_settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "custom crafting/_settings.yml"));
        removeRecipes = config.getStringList("remove recipes");
        recipes = new HashMap<>();

        final Iterator<Recipe> a = SERVER.recipeIterator();
        final List<Recipe> b = new ArrayList<>();
        while(a.hasNext()) {
            final Recipe r = a.next();
            final boolean one = r instanceof ShapedRecipe, two = r instanceof ShapelessRecipe;
            if(!one && !two || one && !((ShapedRecipe) r).getKey().getNamespace().equals("randomsky") || two && !((ShapelessRecipe) r).getKey().getNamespace().equals("randomsky")) {
                final ItemStack result = r.getResult();
                final UMaterial u = UMaterial.match(result.getType().name(), result.getData().getData());
                if(result.getType().equals(Material.AIR) || !removeRecipes.contains(u.name().toLowerCase())) {
                    b.add(r);
                }
            }
        }
        SERVER.clearRecipes();
        for(Recipe r : b) {
            SERVER.addRecipe(r);
        }

        final int size = config.getInt("gui.size");
        gui = new UInventory(null, size, colorize(config.getString("gui.title")));
        final Inventory gi = gui.getInventory();
        background = d(config, "gui.background");
        for(String s : config.getConfigurationSection("gui").getKeys(false)) {
            if(!s.equals("background")) {
                final String p = "gui." + s + ".";
                final int slot = config.getInt(p + "slot");
                final ItemStack display = d(config, "gui." + s);
                gi.setItem(slot, display);
            }
        }
        for(int i = 0; i < size; i++)
            if(gi.getItem(i) == null)
                gi.setItem(i, background);
        int loaded = 0;
        for(String s : config.getConfigurationSection("custom recipes").getKeys(false)) {
            createRecipe(s);
            loaded++;
        }
        final List<UMaterial> doesntWorkAgainst = new ArrayList<>();
        for(String s : config.getConfigurationSection("shields").getKeys(false)) {
            doesntWorkAgainst.clear();
            for(String B : config.getStringList("shields." + s + "doesnt work against")) doesntWorkAgainst.add(UMaterial.match(B));
            new CustomShield(s, colorize(config.getString("shields." + s + ".name")), colorizeListString(config.getStringList("shields." + s + ".lore")), doesntWorkAgainst);
        }
        for(String s : config.getConfigurationSection("bows").getKeys(false)) {
            doesntWorkAgainst.clear();
            for(String B : config.getStringList("bows." + s + "doesnt work against")) doesntWorkAgainst.add(UMaterial.match(B));
            new CustomBow(s, colorize(config.getString("bows." + s + ".name")), colorizeListString(config.getStringList("bows." + s + ".lore")), doesntWorkAgainst);
        }
        sendConsoleMessage("&6[RandomSky] &aLoaded " + loaded + " Custom Crafting Recipes, " + CustomShield.paths.size() + " shields, and " + CustomBow.paths.size() + " bows &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        for(CustomRecipe c : recipes.keySet()) {
            HandlerList.unregisterAll(c);
        }
    }

    protected void createRecipe(String path) {
        final String p = "custom recipes." + path + ".";
        final String type = config.getString(p + "type", "");
        final ItemStack result = d(null, config.getString(p + "result"));
        final String P = path.replace(" ", "_");
        switch (type) {
            case "SHAPED":
                createShapedRecipe(path, p, P, result);
                break;
            case "SHAPELESS":
                createShapelessRecipe(path, p, P, result);
                break;
            default:
                break;
        }
    }
    protected void createShapedRecipe(String path, String p, String P, ItemStack result) {
        final String[] b = new String[10];
        final ItemStack[] a = new ItemStack[10];
        final List<String> format = config.getStringList(p + "recipe.format");
        final HashMap<String, ItemStack> h = new HashMap<>();
        final ShapedRecipe r = new ShapedRecipe(new NamespacedKey(RANDOM_SKY, P + "_shaped"), result);
        for(int i = 0; i < format.size(); i++) {
            final String f = format.get(i);
            for(int o = 0; o < f.length(); o++) {
                final String C = f.substring(o, o+1);
                final ItemStack its = d(null, config.getString(p + "recipe." + C));
                h.put(C, its);
                final int j = i*3+o;
                a[j] = its;
                b[j] = C;
            }
        }
        r.shape(b[0] + b[1] + b[2], b[3] + b[4] + b[5], b[6] + b[7] + b[8]);
        final Map<Character, ItemStack> map = r.getIngredientMap();
        for(String s : h.keySet()) {
            final char c = s.toCharArray()[0];
            final ItemStack y = h.get(s);
            if(y != null && map.get(c) == null) {
                r.setIngredient(c, y.getType());
            }
        }
        new CustomRecipe(path, result, a);
        Bukkit.addRecipe(r);
    }
    protected void createShapelessRecipe(String path, String p, String P, ItemStack result) {
        final ShapelessRecipe r = new ShapelessRecipe(new NamespacedKey(RANDOM_SKY, P + "_shapeless"), result);
        final ItemStack recipe = d(null, config.getString(p + "recipe"));
        final int amount = config.getInt(p + "amount");
        r.addIngredient(1, recipe.getType());
        new CustomRecipe(path, result, recipe, amount);
        Bukkit.addRecipe(r);
    }

    public void viewCrafting(Player player) {
        if(hasPermission(player, "RandomSky.crafting", true)) {
            player.closeInventory();
            player.openInventory(Bukkit.createInventory(player, gui.getSize(), gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());
            player.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final String t = event.getView().getTitle();
        if(gui.getTitle().equals(t)) {
            event.setCancelled(true);
            player.updateInventory();
            final ItemStack c = event.getCurrentItem();
            final int r = event.getRawSlot();
            if(r < 0 || r >= player.getOpenInventory().getTopInventory().getSize() || c == null || c.getType().equals(Material.AIR)) return;
            if(!c.equals(background)) sendStringListMessage(player, config.getStringList("messages.use grid to craft"), null);
        }
    }
    protected class CustomRecipe implements Listener {
        private final String path;
        private final ItemStack result, required;
        private final ItemStack[] recipe;
        private final int amount;
        public CustomRecipe(String path, ItemStack result, ItemStack[] recipe) {
            this.path = path;
            this.result = result;
            this.required = null;
            this.recipe = recipe;
            this.amount = 0;
            PLUGIN_MANAGER.registerEvents(this, RANDOM_SKY);
        }
        public CustomRecipe(String path, ItemStack result, ItemStack required, int amount) {
            this.path = path;
            this.result = result;
            this.required = required;
            this.recipe = null;
            this.amount = amount;
            PLUGIN_MANAGER.registerEvents(this, RANDOM_SKY);
        }

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        private void craftItemEvent(CraftItemEvent event) {
            final Player player = (Player) event.getWhoClicked();
            final CraftingInventory i = event.getInventory();
            final ItemStack r = event.getRecipe().getResult();
            if(r.isSimilar(result)) {
                int A = this.amount;
                final ItemStack[] m = i.getMatrix();
                final boolean shapeless = recipe == null;
                int amount = 0;
                for(int a = 0; a < m.length; a++) {
                    final ItemStack is = m[a];
                    if(is != null) {
                        if(!shapeless && !is.isSimilar(recipe[a])) {
                            event.setCancelled(true);
                            player.updateInventory();
                            return;
                        } else if(shapeless) {
                            if(is.isSimilar(required)) {
                                amount += is.getAmount();
                            } else {
                                event.setCancelled(true);
                                player.updateInventory();
                                return;
                            }
                        }
                    }
                }
                if(shapeless && amount < A) {
                    event.setCancelled(true);
                    player.updateInventory();
                    final String a = Integer.toString(A), n = required != null ? required.hasItemMeta() && required.getItemMeta().hasDisplayName() ? required.getItemMeta().getDisplayName() : toMaterial(required.getType().name(), false) : "null";
                    for(String s : config.getStringList("messages.not enough resources")) {
                        if(s.equals("{REQUIRED_RESOURCES}")) {
                            final List<String> o = colorizeListString(config.getStringList("messages.required resource"));
                            for(String p : o) {
                                player.sendMessage(p.replace("{AMOUNT}", a).replace("{ITEM}", n));
                            }
                        } else {
                            player.sendMessage(colorize(s));
                        }
                    }
                } else {
                    int index = 0, removed = 0;
                    if(event.isShiftClick()) {
                        event.setCancelled(true);
                        final int am = amount/this.amount;
                        item = r.clone();
                        item.setAmount(am);
                        giveItem(player, item);
                        A = am*this.amount+1;
                    }
                    final ItemStack[] mat = m.clone();
                    for(ItemStack is : mat) {
                        if(removed != A) {
                            for(int z = 1; z < A; z++) {
                                if(is != null && removed != A) {
                                    final int am = is.getAmount()-1;
                                    if(am == 0) {
                                        mat[index] = null;
                                    } else {
                                        is.setAmount(am);
                                    }
                                    removed++;
                                }
                            }
                        }
                        index++;
                    }
                    i.setMatrix(mat);
                    player.updateInventory();
                }
            }
        }
    }
}
