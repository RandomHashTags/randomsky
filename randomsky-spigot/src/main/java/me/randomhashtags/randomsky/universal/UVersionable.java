package me.randomhashtags.randomsky.universal;

import me.randomhashtags.randomsky.RandomSky;
import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.supported.economy.Vault;
import me.randomhashtags.randomsky.supported.mechanics.SpawnerAPI;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.Versionable;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.io.File.separator;
import static me.randomhashtags.randomsky.RandomSky.getPlugin;

public interface UVersionable extends Versionable {
    HashMap<Feature, LinkedHashMap<String, Identifiable>> FEATURES = new HashMap<>();
    File DATA_FOLDER = getPlugin.getDataFolder();
    String SEPARATOR = File.separator;

    RandomSky RANDOM_SKY = RandomSky.getPlugin;
    FileConfiguration RP_CONFIG = RANDOM_SKY.getConfig();
    String RP_VERSION = RANDOM_SKY.getDescription().getVersion();
    PluginManager PLUGIN_MANAGER = Bukkit.getPluginManager();
    Server SERVER = Bukkit.getServer();
    Random RANDOM = new Random();

    BukkitScheduler SCHEDULER = Bukkit.getScheduler();
    ScoreboardManager SCOREBOARD_MANAGER = Bukkit.getScoreboardManager();
    ConsoleCommandSender CONSOLE = Bukkit.getConsoleSender();

    Economy ECONOMY = Vault.INSTANCE.getEconomy();

    BlockFace[] BLOCK_FACES = new BlockFace[] { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };
    EquipmentSlot[] EQUIPMENT_SLOTS = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.HAND, EIGHT ? null : EquipmentSlot.OFF_HAND };
    Set<String> INTERACTABLE_MATERIALS = new HashSet<String>() {{
        addAll(Arrays.asList(
                "BELL",
                "BREWING_STAND",
                "BLAST_FURNACE",
                "CAULDRON",
                "CHEST",
                "COMPOSTER",
                "CRAFTING_TABLE", "WORKBENCH",
                "DISPENSER",
                "DROPPER",
                "ENDER_CHEST",
                "ENCHANTMENT_TABLE", "ENCHANTING_TABLE",
                "FENCE_GATE",
                "FURNACE",
                "GRINDSTONE",
                "HOPPER",
                "JUKEBOX",
                "LECTERN",
                "LOOM",
                "LEVER",
                "NOTE_BLOCK",
                "SMOKER",
                "STONECUTTER",
                "TNT",
                "TRAPDOOR",
                "TRAPPED_CHEST",
                //
                "BED"
        ));
    }};

    HashMap<FileConfiguration, HashMap<String, List<String>>> FEATURE_MESSAGES = new HashMap<>();

    default List<String> getStringList(FileConfiguration yml, String identifier) {
        if(!FEATURE_MESSAGES.containsKey(yml)) {
            FEATURE_MESSAGES.put(yml, new HashMap<>());
        }
        final HashMap<String, List<String>> messages = FEATURE_MESSAGES.get(yml);
        if(!messages.containsKey(identifier)) {
            messages.put(identifier, colorizeListString(yml.getStringList(identifier)));
        }
        return messages.get(identifier);
    }

    default void save(@Nullable String folder, @NotNull String file) {
        File f;
        final File d = RANDOM_SKY.getDataFolder();
        if(folder != null && !folder.equals("")) {
            f = new File(d + separator + folder + separator, file);
        } else {
            f = new File(d + separator, file);
        }
        if(!f.exists()) {
            f.getParentFile().mkdirs();
            RANDOM_SKY.saveResource(folder != null && !folder.equals("") ? folder + separator + file : file, false);
        }
        if(folder == null || !folder.equals("_Data")) {
            //updateYaml(f); // TODO: fix?
        }
    }

    @Nullable
    default ItemStack getClone(@Nullable ItemStack is) {
        return getClone(is, null);
    }
    @Nullable
    default ItemStack getClone(@Nullable ItemStack is, @Nullable ItemStack def) {
        return is != null ? is.clone() : def;
    }

    default int getTotalExperience(@NotNull Player player) {
        final double levelxp = LevelToExp(player.getLevel()), nextlevelxp = LevelToExp(player.getLevel() + 1), difference = nextlevelxp - levelxp;
        final double p = (levelxp + (difference * player.getExp()));
        return (int) Math.round(p);
    }
    default void setTotalExperience(@NotNull Player player, int total) {
        player.setTotalExperience(0);
        player.setExp(0f);
        player.setLevel(0);
        player.giveExp(total);
    }
    default double LevelToExp(int level) {
        return level <= 16 ? (level * level) + (level * 6) : level <= 31 ? (2.5 * level * level) - (40.5 * level) + 360 : (4.5 * level * level) - (162.5 * level) + 2220;
    }
    @NotNull
    default String getRemainingTime(long time) {
        int sec = (int) TimeUnit.MILLISECONDS.toSeconds(time), min = sec/60, hr = min/60, d = hr/24;
        hr -= d*24;
        min -= (hr*60)+(d*60*24);
        sec -= (min*60)+(hr*60*60)+(d*60*60*24);
        final String dys = d > 0 ? d + "d" + (hr != 0 ? " " : "") : "";
        final String hrs = hr > 0 ? hr + "h" + (min != 0 ? " " : "") : "";
        final String mins = min != 0 ? min + "m" + (sec != 0 ? " " : "") : "";
        final String secs = sec != 0 ? sec + "s" : "";
        return dys + hrs + mins + secs;
    }
    default void sendConsoleMessage(@NotNull String msg) {
        CONSOLE.sendMessage(colorize(msg));
    }
    @NotNull
    default String formatBigDecimal(@NotNull BigDecimal b) {
        return formatBigDecimal(b, false);
    }
    @NotNull
    default String formatBigDecimal(@NotNull BigDecimal b, boolean currency) {
        return (currency ? NumberFormat.getCurrencyInstance() : NumberFormat.getInstance()).format(b);
    }
    @NotNull
    default BigDecimal valueOfBigDecimal(@NotNull String input) {
        final long m = input.endsWith("k") ? 1000 : input.endsWith("m") ? 1000000 : input.endsWith("b") ? 1000000000 : 1;
        return BigDecimal.valueOf(getRemainingDouble(input)*m);
    }
    @NotNull
    default BigDecimal getBigDecimal(@NotNull String value) {
        return BigDecimal.valueOf(Double.parseDouble(value));
    }
    @NotNull
    default BigDecimal getRandomBigDecimal(@NotNull BigDecimal min, @NotNull BigDecimal max) {
        final BigDecimal range = max.subtract(min);
        return min.add(range.multiply(new BigDecimal(Math.random())));
    }
    @NotNull
    default String formatDouble(double d) {
        String decimals = Double.toString(d).split("\\.")[1];
        if(decimals.equals("0")) { decimals = ""; } else { decimals = "." + decimals; }
        return formatInt((int) d) + decimals;
    }
    @NotNull
    default String formatLong(long l) {
        final String f = Long.toString(l);
        final boolean c = f.contains(".");
        String decimals = c ? f.split("\\.")[1] : f;
        decimals = c ? decimals.equals("0") ? "" : "." + decimals : "";
        return formatInt((int) l) + decimals;
    }
    @NotNull
    default String formatInt(int integer) {
        return String.format("%,d", integer);
    }
    default int getRemainingInt(@NotNull String string) {
        string = ChatColor.stripColor(colorize(string)).replaceAll("\\p{L}", "").replaceAll("\\s", "").replaceAll("\\p{P}", "").replaceAll("\\p{S}", "");
        return string.isEmpty() ? -1 : Integer.parseInt(string);
    }
    default Double getRemainingDouble(@NotNull String string) {
        string = ChatColor.stripColor(colorize(string).replaceAll("\\p{L}", "").replaceAll("\\p{Z}", "").replaceAll("\\.", "d").replaceAll("\\p{P}", "").replaceAll("\\p{S}", "").replace("d", "."));
        return string.isEmpty() ? -1.00 : Double.parseDouble(string.contains(".") && string.split("\\.").length > 1 && string.split("\\.")[1].length() > 2 ? string.substring(0, string.split("\\.")[0].length() + 3) : string);
    }
    default long getDelay(@NotNull String input) {
        input = input.toLowerCase();
        long l = 0;
        if(input.contains("d")) {
            final String[] s = input.split("d");
            l += getRemainingDouble(s[0])*1000*60*60*24;
            input = s.length > 1 ? s[1] : input;
        }
        if(input.contains("h")) {
            final String[] s = input.split("h");
            l += getRemainingDouble(s[0])*1000*60*60;
            input = s.length > 1 ? s[1] : input;
        }
        if(input.contains("m")) {
            final String[] s = input.split("m");
            l += getRemainingDouble(s[0])*1000*60;
            input = s.length > 1 ? s[1] : input;
        }
        if(input.contains("s")) {
            l += getRemainingDouble(input.split("s")[0])*1000;
        }
        return l;
    }

    @Nullable
    default EquipmentSlot getRespectiveSlot(@NotNull String material) {
        return material.contains("HELMET") || material.contains("SKULL") || material.contains("HEAD") ? EquipmentSlot.HEAD
                : material.contains("CHESTPLATE") || material.contains("ELYTRA") ? EquipmentSlot.CHEST
                : material.contains("LEGGINGS") ? EquipmentSlot.LEGS
                : material.contains("BOOTS") ? EquipmentSlot.FEET
                : null;
    }
    default double round(double input, int decimals) {
        // From http://www.baeldung.com/java-round-decimal-number
        if(decimals < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(Double.toString(input));
        bd = bd.setScale(decimals, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    @NotNull
    default String roundDoubleString(double input, int decimals) {
        final double d = round(input, decimals);
        return Double.toString(d);
    }

    @Nullable
    default String center(String s, int size) {
        // Credit to "Sahil Mathoo" from StackOverFlow at https://stackoverflow.com/questions/8154366
        return center(s, size, ' ');
    }
    @Nullable
    default String center(String s, int size, char pad) {
        if(s == null || size <= s.length()) {
            return s;
        }
        final StringBuilder sb = new StringBuilder(size);
        for(int i = 0; i < (size - s.length()) / 2; i++) {
            sb.append(pad);
        }
        sb.append(s);
        while(sb.length() < size) {
            sb.append(pad);
        }
        return sb.toString();
    }
    @NotNull
    default List<String> colorizeListString(@Nullable List<String> input) {
        final List<String> i = new ArrayList<>();
        if(input != null) {
            for(String s : input) {
                i.add(colorize(s));
            }
        }
        return i;
    }
    @NotNull
    default String colorize(@Nullable String input) {
        return input != null ? ChatColor.translateAlternateColorCodes('&', input) : "NULL";
    }
    default void sendStringListMessage(@NotNull CommandSender sender, @Nullable List<String> message, @Nullable HashMap<String, String> replacements) {
        if(message != null && message.size() > 0 && !message.get(0).equals("")) {
            final boolean papi = RANDOM_SKY.placeholder_api_is_enabled, isPlayer = sender instanceof Player;
            final Player player = isPlayer ? (Player) sender : null;
            for(String s : message) {
                if(replacements != null) {
                    for(String r : replacements.keySet()) {
                        final String replacement = replacements.get(r);
                        s = s.replace(r, replacement != null ? replacement : "null");
                    }
                }
                if(s != null) {
                    if(papi && isPlayer) {
                        s = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, s);
                    }
                    sender.sendMessage(colorize(s));
                }
            }
        }
    }



    default boolean isPassive(@NotNull EntityType type) {
        if(type.isSpawnable()) {
            switch (type.name().toLowerCase()) {
                case "bat":
                case "cat":
                case "chicken":
                case "cod":
                case "cow":
                case "dolphin":
                case "donkey":
                case "fox":
                case "horse":
                case "player":
                case "llama":
                case "mule":
                case "mushroom_cow":
                case "ocelot":
                case "panda":
                case "parrot":
                case "pig":
                case "pufferfish":
                case "rabbit":
                case "salmon":
                case "sheep":
                case "squid":
                case "tropical_fish":
                case "turtle":
                case "villager":
                case "wandering_trader":
                case "zombie_horse":
                    return true;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }
    default boolean isAggressive(@NotNull EntityType type) {
        return !isPassive(type);
    }
    default boolean isNeutral(@NotNull EntityType type) {
        if(type.isSpawnable() && !isPassive(type)) {
            switch (type.name()) {
                case "enderman":
                case "iron_golem":
                case "polar_bear":
                case "wolf": return true;
            }
        }
        return false;
    }
    @Nullable
    default PotionEffectType getPotionEffectType(@Nullable String input) {
        if(input != null && !input.isEmpty()) {
            switch (input.toUpperCase()) {
                case "STRENGTH": return PotionEffectType.INCREASE_DAMAGE;
                case "MINING_FATIGUE": return PotionEffectType.SLOW_DIGGING;
                case "SLOWNESS": return PotionEffectType.SLOW;
                case "HASTE": return PotionEffectType.FAST_DIGGING;
                case "JUMP": return PotionEffectType.JUMP;
                case "INSTANT_HEAL":
                case "INSTANT_HEALTH": return PotionEffectType.HEAL;
                case "INSTANT_HARM":
                case "INSTANT_DAMAGE": return PotionEffectType.HARM;
                default:
                    for(PotionEffectType p : PotionEffectType.values()) {
                        if(p != null && input.equalsIgnoreCase(p.getName())) {
                            return p;
                        }
                    }
                    return null;
            }
        } else return null;
    }

    @NotNull
    default String toString(@NotNull Location loc) {
        return loc.getWorld().getName() + ";" + loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch();
    }
    @Nullable
    default Location toLocation(@Nullable String string) {
        if(string != null && string.contains(";")) {
            final String[] a = string.split(";");
            return new Location(Bukkit.getWorld(a[0]), Double.parseDouble(a[1]), Double.parseDouble(a[2]), Double.parseDouble(a[3]), Float.parseFloat(a[4]), Float.parseFloat(a[5]));
        } else {
            return null;
        }
    }

    default void giveItem(Player player, ItemStack is) {
        if(is == null || is.getType().equals(Material.AIR)) return;
        final UMaterial m = UMaterial.match(is);
        final ItemMeta meta = is.getItemMeta();
        final PlayerInventory i = player.getInventory();
        final int f = i.first(is.getType()), e = i.firstEmpty(), max = is.getMaxStackSize();
        int amountLeft = is.getAmount();

        if(f != -1) {
            for(int s = 0; s < i.getSize(); s++) {
                final ItemStack t = i.getItem(s);
                if(amountLeft > 0 && t != null && t.getItemMeta().equals(meta) && UMaterial.match(t) == m) {
                    final int a = t.getAmount(), toMax = max-a, given = Math.min(amountLeft, toMax);
                    if(given > 0) {
                        t.setAmount(a+given);
                        amountLeft -= given;
                    }
                }
            }
            player.updateInventory();
        }
        if(amountLeft > 0) {
            is.setAmount(amountLeft);
            if(e >= 0) {
                i.addItem(is);
                player.updateInventory();
            } else {
                player.getWorld().dropItem(player.getLocation(), is);
            }
        }
    }

    @NotNull
    default BlockFace getFacing(Entity entity) {
        return LEGACY || THIRTEEN ? BLOCK_FACES[Math.round(entity.getLocation().getYaw() / 45f) & 0x7] : entity.getFacing();
    }
    default String toReadableDate(Date d, String format) {
        return new SimpleDateFormat(format).format(d);
    }
    @Nullable
    default Entity getEntity(@Nullable UUID uuid) {
        if(uuid != null) {
            if(EIGHT || NINE) {
                for(World w : Bukkit.getWorlds()) {
                    for(LivingEntity le : w.getLivingEntities()) {
                        if(uuid.equals(le.getUniqueId())) {
                            return le;
                        }
                    }
                }
            } else {
                return Bukkit.getEntity(uuid);
            }
        }
        return null;
    }
    default LivingEntity getEntity(String type, Location l, boolean spawn) {
        final boolean baby = type.contains(":") && type.toLowerCase().endsWith(":true");
        type = type.toUpperCase().split(":")[0];
        final LivingEntity mob = getEntity(type, l);
        if(mob instanceof Zombie) {
            ((Zombie) mob).setBaby(baby);
        }
        if(!spawn) {
            mob.remove();
        } else if(mob instanceof Ageable && baby) {
            final Ageable a = (Ageable) mob;
            a.setBaby();
            a.setAgeLock(true);
        }
        return mob;
    }

    @Nullable
    default Color getColor(@Nullable String path) {
        if(path == null) {
            return null;
        }
        switch (path.toLowerCase()) {
            case "aqua": return Color.AQUA;
            case "black": return Color.BLACK;
            case "blue": return Color.BLUE;
            case "fuchsia": return Color.FUCHSIA;
            case "gray": return Color.GRAY;
            case "green": return Color.GREEN;
            case "lime": return Color.LIME;
            case "maroon": return Color.MAROON;
            case "navy": return Color.NAVY;
            case "olive": return Color.OLIVE;
            case "orange": return Color.ORANGE;
            case "purple": return Color.PURPLE;
            case "red": return Color.RED;
            case "silver": return Color.SILVER;
            case "teal": return Color.TEAL;
            case "white": return Color.WHITE;
            case "yellow": return Color.YELLOW;
            default: return null;
        }
    }
    default boolean isInteractable(@NotNull Material material) {
        final String m = material.name();
        if(!LEGACY) {
            return material.isInteractable();
        } else {
            return INTERACTABLE_MATERIALS.contains(m) || m.contains("ANVIL") || m.endsWith("_BED")
                    || m.endsWith("DOOR") && !m.equals("IRON_DOOR")
                    ;
        }
    }

    default void didApply(@NotNull InventoryClickEvent event, @NotNull Player player, ItemStack current, @NotNull ItemStack cursor) {
        event.setCancelled(true);
        final int a = cursor.getAmount();
        if(a == 1) {
            event.setCursor(new ItemStack(Material.AIR));
        } else {
            cursor.setAmount(a-1);
            event.setCursor(cursor);
        }
        player.updateInventory();
    }

    default void spawnFirework(Firework firework, Location loc) {
        if(firework != null) {
            final Firework fw = loc.getWorld().spawn(new Location(loc.getWorld(), loc.getX()+0.5, loc.getY(), loc.getZ()+0.5), Firework.class);
            fw.setFireworkMeta(firework.getFireworkMeta());
        }
    }
    default Firework createFirework(FireworkEffect.Type explosionType, Color trailColor, Color explodeColor, int power) {
        final World w = Bukkit.getWorlds().get(0);
        final Firework fw = w.spawn(w.getSpawnLocation(), Firework.class);
        final FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(power);
        fwm.addEffect(FireworkEffect.builder().trail(true).flicker(true).with(explosionType).withColor(trailColor).withFade(explodeColor).withFlicker().withTrail().build());
        fw.setFireworkMeta(fwm);
        return fw;
    }

    default String toMaterial(String input, boolean realitem) {
        if(input.contains(":")) {
            input = input.split(":")[0];
        }
        if(input.contains(" ")) {
            input = input.replace(" ", "");
        }
        if(input.contains("_")) {
            input = input.replace("_", " ");
        }
        String e = "";
        if(input.contains(" ")) {
            final String[] spaces = input.split(" ");
            final int l = spaces.length;
            for(int i = 0; i < l; i++) {
                e = e + spaces[i].substring(0, 1).toUpperCase() + spaces[i].substring(1).toLowerCase() + (i != l-1 ? (realitem ? "_" : " ") : "");
            }
        } else e = input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
        return e;
    }

    default Enchantment getEnchantment(String string) {
        if(string != null) {
            for(Enchantment enchant : Enchantment.values()) {
                final String name = enchant != null ? enchant.getName() : null;
                if(name != null && string.toLowerCase().replace("_", "").startsWith(name.toLowerCase().replace("_", ""))) {
                    return enchant;
                }
            }
            string = string.toLowerCase().replace("_", "");
            if(string.startsWith("po")) { return Enchantment.ARROW_DAMAGE; // Power
            } else if(string.startsWith("fl")) { return Enchantment.ARROW_FIRE; // Flame
            } else if(string.startsWith("i")) { return Enchantment.ARROW_INFINITE; // Infinity
            } else if(string.startsWith("pu")) { return Enchantment.ARROW_KNOCKBACK; // Punch
            } else if(string.startsWith("bi") && !EIGHT && !NINE && !TEN) { return Enchantment.getByName("BINDING_CURSE"); // Binding Curse
            } else if(string.startsWith("sh")) { return Enchantment.DAMAGE_ALL; // Sharpness
            } else if(string.startsWith("ba")) { return Enchantment.DAMAGE_ARTHROPODS; // Bane of Arthropods
            } else if(string.startsWith("sm")) { return Enchantment.DAMAGE_UNDEAD; // Smite
            } else if(string.startsWith("de")) { return Enchantment.DEPTH_STRIDER; // Depth Strider
            } else if(string.startsWith("e")) { return Enchantment.DIG_SPEED; // Efficiency
            } else if(string.startsWith("u")) { return Enchantment.DURABILITY; // Unbreaking
            } else if(string.startsWith("firea")) { return Enchantment.FIRE_ASPECT; // Fire Aspect
            } else if(string.startsWith("fr") && !EIGHT) { return Enchantment.getByName("FROST_WALKER"); // Frost Walker
            } else if(string.startsWith("k")) { return Enchantment.KNOCKBACK; // Knockback
            } else if(string.startsWith("fo")) { return Enchantment.LOOT_BONUS_BLOCKS; // Fortune
            } else if(string.startsWith("lo")) { return Enchantment.LOOT_BONUS_MOBS; // Looting
            } else if(string.startsWith("luc")) { return Enchantment.LUCK; // Luck
            } else if(string.startsWith("lur")) { return Enchantment.LURE; // Lure
            } else if(string.startsWith("m") && !EIGHT) { return Enchantment.getByName("MENDING"); // Mending
            } else if(string.startsWith("r")) { return Enchantment.OXYGEN; // Respiration
            } else if(string.startsWith("prot")) { return Enchantment.PROTECTION_ENVIRONMENTAL; // Protection
            } else if(string.startsWith("bl") || string.startsWith("bp")) { return Enchantment.PROTECTION_EXPLOSIONS; // Blast Protection
            } else if(string.startsWith("ff") || string.startsWith("fe")) { return Enchantment.PROTECTION_FALL; // Feather Falling
            } else if(string.startsWith("fp") || string.startsWith("firep")) { return Enchantment.PROTECTION_FIRE; // Fire Protection
            } else if(string.startsWith("pp") || string.startsWith("proj")) { return Enchantment.PROTECTION_PROJECTILE; // Projectile Protection
            } else if(string.startsWith("si")) { return Enchantment.SILK_TOUCH; // Silk Touch
            } else if(string.startsWith("th")) { return Enchantment.THORNS; // Thorns
            } else if(string.startsWith("v") && !EIGHT && !NINE && !TEN) { return Enchantment.getByName("VANISHING_CURSE"); // Vanishing Curse
            } else if(string.startsWith("aa") || string.startsWith("aq")) { return Enchantment.WATER_WORKER; // Aqua Affinity
            } else { return null; }
        }
        return null;
    }

    default int indexOf(Set<? extends Object> collection, Object value) {
        int i = 0;
        for(Object o : collection) {
            if(value.equals(o)) return i;
            i++;
        }
        return -1;
    }

    default void removeItem(Player player, ItemStack itemstack, int amount) {
        final PlayerInventory inv = player.getInventory();
        int nextslot = getNextSlot(player, itemstack);
        for(int i = 1; i <= amount; i++) {
            if(nextslot >= 0) {
                final ItemStack is = inv.getItem(nextslot);
                if(is.getAmount() == 1) {
                    inv.setItem(nextslot, new ItemStack(Material.AIR));
                    nextslot = getNextSlot(player, itemstack);
                } else {
                    is.setAmount(is.getAmount() - 1);
                }
            }
        }
        player.updateInventory();
    }
    private int getNextSlot(Player player, ItemStack itemstack) {
        final PlayerInventory inv = player.getInventory();
        for(int i = 0; i < inv.getSize(); i++) {
            final ItemStack item = inv.getItem(i);
            if(item != null && item.isSimilar(itemstack)) {
                return i;
            }
        }
        return -1;
    }
    default int getTotalAmount(Inventory inventory, UMaterial umat) {
        final ItemStack i = umat.getItemStack();
        int amount = 0;
        for(ItemStack is : inventory.getContents()) {
            if(is != null && is.isSimilar(i)) {
                amount += is.getAmount();
            }
        }
        return amount;
    }

    @Nullable
    default ItemStack getSpawner(@NotNull String input) {
        String pi = input.toLowerCase(), type = null;
        if(pi.equals("mysterymobspawner")) {
            return givedpitem.valueOf("mysterymobspawner").clone();
        } else {
            if(RandomSky.spawnerPlugin != null) {
                for(EntityType entitytype : EntityType.values()) {
                    final String s = entitytype.name().toLowerCase().replace("_", "");
                    if(pi.startsWith(s + "spawner")) {
                        type = s;
                    }
                }
                if(type == null) {
                    if(pi.contains("pig") && pi.contains("zombie")) type = "pigzombie";
                }
                if(type == null) return null;
                final ItemStack is = SpawnerAPI.getSpawnerAPI().getItem(type);
                if(is != null) {
                    return is;
                } else {
                    CONSOLE.sendMessage("[RandomPackage] SilkSpawners or EpicSpawners is required to use this feature!");
                }
            }
        }
        return null;
    }

    @Nullable
    default Entity getHitEntity(@NotNull ProjectileHitEvent event) {
        if(EIGHT || NINE || TEN) {
            final List<Entity> n = event.getEntity().getNearbyEntities(0.1, 0.1, 0.1);
            return n.size() > 0 ? n.get(0) : null;
        } else {
            return event.getHitEntity();
        }
    }
    default void playParticle(@NotNull FileConfiguration config, @NotNull String path, @NotNull Location location, int count) {
        final String target = config.getString(path);
        if(target != null) {
            final UParticle up = UParticle.matchParticle(target.toUpperCase());
            if(up != null) {
                up.play(location, count);
            }
        }
    }
    default void playSound(FileConfiguration config, String path, Player player, Location location, boolean globalsound) {
        if(config.get(path) != null) {
            final String[] p = config.getString(path).split(":");
            final String s = p[0].toUpperCase();
            final int v = Integer.parseInt(p[1]), pp = Integer.parseInt(p[2]);
            try {
                final Sound sound = Sound.valueOf(s);
                if(player != null) {
                    if(!globalsound) {
                        player.playSound(location, sound, v, pp);
                    } else {
                        player.getWorld().playSound(location, sound, v, pp);
                    }
                } else {
                    location.getWorld().playSound(location, sound, v, pp);
                }
            } catch (Exception e) {
                sendConsoleMessage("&6[RandomPackage] &cERROR! Invalid sound name: &f" + s + "&c!");
            }
        }
    }

    @NotNull
    default List<Location> getChunkLocations(@NotNull Chunk chunk) {
        final List<Location> l = new ArrayList<>();
        final int x = chunk.getX()*16, z = chunk.getZ()*16;
        final World world = chunk.getWorld();
        for(int xx = x; xx < x+16; xx++) {
            for(int zz = z; zz < z+16; zz++) {
                l.add(new Location(world, xx, 0, zz));
            }
        }
        return l;
    }
    @Nullable
    default ItemStack getItemInHand(@Nullable LivingEntity entity) {
        if(entity == null) {
            return null;
        } else {
            final EntityEquipment e = entity.getEquipment();
            return EIGHT ? e.getItemInHand() : e.getItemInMainHand();
        }
    }
    @Nullable
    default LivingEntity getEntity(@NotNull String type, @NotNull Location l) {
        final World w = l.getWorld();
        final LivingEntity le;
        switch (type.toUpperCase()) {
            case "BAT": return w.spawn(l, Bat.class);
            case "BLAZE": return w.spawn(l, Blaze.class);
            case "CAVE_SPIDER": return w.spawn(l, CaveSpider.class);
            case "CHICKEN": return w.spawn(l, Chicken.class);
            case "COW": return w.spawn(l, Cow.class);
            case "CREEPER": return w.spawn(l, Creeper.class);
            case "ENDER_DRAGON": return w.spawn(l, EnderDragon.class);
            case "ENDERMAN": return w.spawn(l, Enderman.class);
            case "GHAST": return w.spawn(l, Ghast.class);
            case "GIANT": return w.spawn(l, Giant.class);
            case "GUARDIAN": return w.spawn(l, Guardian.class);
            case "HORSE": return w.spawn(l, Horse.class);
            case "IRON_GOLEM": return w.spawn(l, IronGolem.class);
            case "LLAMA": return EIGHT || NINE || TEN ? null : w.spawn(l, Llama.class);
            case "MAGMA_CUBE": return w.spawn(l, MagmaCube.class);
            case "MUSHROOM_COW": return w.spawn(l, MushroomCow.class);
            case "OCELOT": return w.spawn(l, Ocelot.class);
            case "PARROT": return EIGHT || NINE || TEN || ELEVEN ? null : w.spawn(l, Parrot.class);
            case "PIG": return w.spawn(l, Pig.class);
            case "PIG_ZOMBIE": return w.spawn(l, PigZombie.class);
            case "RABBIT": return w.spawn(l, Rabbit.class);
            case "SHEEP": return w.spawn(l, Sheep.class);
            case "SHULKER": return EIGHT ? null : w.spawn(l, Shulker.class);
            case "SILVERFISH": return w.spawn(l, Silverfish.class);
            case "SKELETON": return w.spawn(l, Skeleton.class);
            case "SLIME": return w.spawn(l, Slime.class);
            case "SNOWMAN": return w.spawn(l, Snowman.class);
            case "SQUID": return w.spawn(l, Squid.class);
            case "SPIDER": return w.spawn(l, Spider.class);
            case "STRAY": return EIGHT || NINE ? null : w.spawn(l, Stray.class);
            case "VEX": return EIGHT || NINE || TEN ? null : w.spawn(l, Vex.class);
            case "VILLAGER": return w.spawn(l, Villager.class);
            case "VINDICATOR": return EIGHT || NINE || TEN ? null : w.spawn(l, Vindicator.class);
            case "WITHER_SKELETON":
                if(EIGHT || NINE || TEN) {
                    le = w.spawn(l, Skeleton.class);
                    ((Skeleton) le).setSkeletonType(Skeleton.SkeletonType.WITHER);
                    return le;
                } else {
                    return w.spawn(l, WitherSkeleton.class);
                }
            case "ZOMBIE": return w.spawn(l, Zombie.class);
            case "ZOMBIE_HORSE": return EIGHT ? null : w.spawn(l, ZombieHorse.class);
            case "ZOMBIE_VILLAGER": return EIGHT ? null : w.spawn(l, ZombieVillager.class);
            default: return null;
        }
    }
}
