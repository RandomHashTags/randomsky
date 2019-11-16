package me.randomhashtags.randomsky.addon.file;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randomsky.addon.FarmingRecipe;
import me.randomhashtags.randomsky.addon.ResourceNode;
import me.randomhashtags.randomsky.addon.island.Island;
import me.randomhashtags.randomsky.addon.island.IslandLevel;
import me.randomhashtags.randomsky.addon.island.IslandOrigin;
import me.randomhashtags.randomsky.addon.island.IslandRank;
import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSAddon;
import me.randomhashtags.randomsky.util.RSStorage;
import me.randomhashtags.randomsky.util.obj.PolyBoundary;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.*;

import static java.io.File.separator;

public class FileIsland extends RSAddon implements Island {
    private UUID uuid, creator;
    private boolean isLoaded, isOpenToPublic;
    private String[] info, boolSettings, stringSettings;
    private IslandLevel level;
    private IslandOrigin origin;
    private PolyBoundary currentBoundary, maxBoundary;
    private String tag, desc;
    private List<String> tags, allowedMobs;

    private List<FarmingRecipe> allowedCrops;
    private List<ResourceNode> allowedNodes;

    private HashMap<UUID, Double> ratings;
    private HashMap<UUID, IslandRank> members;

    private TreeMap<String, Location> locations;
    private List<UUID> bannedPlayers;

    public FileIsland(File f) {
        load(f);
        uuid = UUID.fromString(getYamlName());
        RSStorage.register(Feature.ISLAND, this);
        load();
    }
    public static FileIsland get(@NotNull UUID uuid) {
        final String u = uuid.toString();
        final Identifiable target = RSStorage.get(Feature.ISLAND, u);
        return target != null ? (FileIsland) target : new FileIsland(new File(dataFolder + separator + "_Data" + separator + "players", u + ".yml"));
    }

    public String getIdentifier() { return getYamlName(); }
    public boolean isLoaded() { return isLoaded; }

    private String[] getInfo() {
        if(info == null) {
            info = yml.getString("info").split(";");
        }
        return info;
    }
    private String[] getBoolSettings() {
        if(boolSettings == null) {
            boolSettings = yml.getString("settings.bool").split(";");
        }
        return boolSettings;
    }
    private String[] getStringSettings() {
        if(stringSettings == null) {
            stringSettings = yml.getString("settings.string").split(";");
        }
        return stringSettings;
    }

    public UUID getUUID() { return uuid; }
    public long getCreatedTime() { return Long.parseLong(getInfo()[0]); }
    public UUID getCreator() {
        if(creator == null) {
            creator = UUID.fromString(getInfo()[1]);
        }
        return creator;
    }
    public boolean isOpenToPublic() { return isOpenToPublic; }
    public void setOpenToPublic(boolean isOpenToPublic) { this.isOpenToPublic = isOpenToPublic; }
    public IslandLevel getIslandLevel() {
        if(level == null) {
            level = (IslandLevel) RSStorage.get(Feature.ISLAND_LEVEL, getStringSettings()[0]);
        }
        return level;
    }
    public IslandOrigin getOrigin() {
        if(origin == null) {
            origin = (IslandOrigin) RSStorage.get(Feature.ISLAND_ORIGIN, getStringSettings()[1]);
        }
        return origin;
    }
    public PolyBoundary getCurrentBoundary() {
        if(currentBoundary == null) {
            final String[] pos = yml.getString("settings.boundary.current.size").split(";");
            final int x = Integer.parseInt(pos[0]), y = Integer.parseInt(pos[1]), z = Integer.parseInt(pos[2]);
            currentBoundary = new PolyBoundary(yml.getLocation("settings.boundary.current.center"), x, y, z);
        }
        return currentBoundary;
    }
    public PolyBoundary getMaxBoundary() {
        if(maxBoundary == null) {
            final String[] pos = yml.getString("settings.boundary.max.size").split(";");
            final int x = Integer.parseInt(pos[0]), y = Integer.parseInt(pos[1]), z = Integer.parseInt(pos[2]);
            maxBoundary = new PolyBoundary(yml.getLocation("settings.boundary.max.center"), x, y, z);
        }
        return maxBoundary;
    }

    public String getTag() {
        if(tag == null) {
            tag = getStringSettings()[2];
        }
        return tag;
    }
    public String getDescription() {
        if(desc == null) {
            desc = getStringSettings()[3];
        }
        return desc;
    }
    public List<String> getTags() {
        if(tags == null) {
            tags = yml.getStringList("settings.tags");
        }
        return tags;
    }

    public List<FarmingRecipe> getAllowedCrops() {
        if(allowedCrops == null) {
            allowedCrops = new ArrayList<>();
            final List<String> a = yml.getStringList("allowed crops");
            for(String s : a) {
                final Identifiable i = RSStorage.get(Feature.FARMING_RECIPE, s);
                if(i != null) {
                    allowedCrops.add((FarmingRecipe) i);
                }
            }
        }
        return allowedCrops;
    }
    public List<ResourceNode> getAllowedNodes() {
        if(allowedNodes == null) {
            allowedNodes = new ArrayList<>();
            final List<String> a = yml.getStringList("allowed nodes");
            for(String s : a) {
                final Identifiable i = RSStorage.get(Feature.RESOURCE_NODE, s);
                if(i != null) {
                    allowedNodes.add((ResourceNode) i);
                }
            }
        }
        return allowedNodes;
    }
    public List<String> getAllowedMobs() {
        if(allowedMobs == null) {
            allowedMobs = yml.getStringList("allowed mobs");
        }
        return allowedMobs;
    }

    public HashMap<UUID, Double> getRatings() {
        if(ratings == null) {
            ratings = new HashMap<>();
            final ConfigurationSection c = yml.getConfigurationSection("ratings");
            if(c != null) {
                for(String s : c.getKeys(false)) {
                    ratings.put(UUID.fromString(s), yml.getDouble("ratings." + s));
                }
            }
        }
        return ratings;
    }
    public HashMap<UUID, IslandRank> getMembers() {
        if(members == null) {
            members = new HashMap<>();
            final ConfigurationSection c = yml.getConfigurationSection("members");
            if(c != null) {
                for(String s : c.getKeys(false)) {
                    final Identifiable rank = RSStorage.get(Feature.ISLAND_RANK, yml.getString("members." + s + ".rank"));
                    if(rank != null) {
                        members.put(UUID.fromString(s), (IslandRank) rank);
                    }
                }
            }
        }
        return members;
    }

    public TreeMap<String, Location> getLocations() {
        if(locations == null) {
            locations = new TreeMap<>();
            final ConfigurationSection c = yml.getConfigurationSection("locations");
            if(c != null) {
                for(String s : c.getKeys(false)) {
                    locations.put(s, yml.getLocation("locations." + s));
                }
            }
        }
        return locations;
    }

    public List<UUID> getBannedPlayers() {
        if(bannedPlayers == null) {
            bannedPlayers = new ArrayList<>();
            for(String s : yml.getStringList("banned players")) {
                bannedPlayers.add(UUID.fromString(s));
            }
        }
        return bannedPlayers;
    }

    public void load() {
        if(!isLoaded) {
            isLoaded = true;

            final String[] booleans = getBoolSettings();
            isOpenToPublic = Boolean.parseBoolean(booleans[0]);
        }
    }
    public void unload() {
        if(isLoaded) {
            isLoaded = false;

            if(boolSettings != null) {
                final String bools = isOpenToPublic + ";";
                yml.set("settings.bool", bools);
            }
            if(stringSettings != null) {
                final String string = getIslandLevel().getIdentifier() + ";" + getOrigin().getIdentifier() + ";" + getTag() + ";" + getDescription();
                yml.set("settings.string", string);
            }

            if(currentBoundary != null) {
                yml.set("settings.boundary.current.center", currentBoundary.getCenter().toString());
                yml.set("settings.boundary.current.size", currentBoundary.getX() + ";" + currentBoundary.getY() + ";" + currentBoundary.getZ());
            }
            if(maxBoundary != null) {
                yml.set("settings.boundary.max.center", maxBoundary.getCenter().toString());
                yml.set("settings.boundary.max.size", maxBoundary.getX() + ";" + maxBoundary.getY() + ";" + maxBoundary.getZ());
            }

            if(tags != null) {
                yml.set("settings.tags", tags);
            }

            if(allowedCrops != null) {
                final List<String> crops = new ArrayList<>();
                for(FarmingRecipe r : allowedCrops) {
                    crops.add(r.getIdentifier());
                }
                yml.set("allowed crops", crops);
            }
            if(allowedNodes != null) {
                final List<String> nodes = new ArrayList<>();
                for(ResourceNode n : allowedNodes) {
                    nodes.add(n.getIdentifier());
                }
                yml.set("allowed nodes", nodes);
            }
            if(allowedMobs != null) {
                yml.set("allowed mobs", allowedMobs);
            }

            if(ratings != null) {
                yml.set("ratings", ratings.toString());
            }
            if(members != null) {
                yml.set("members", members.toString());
            }

            if(locations != null) {
                yml.set("locations", locations.toString());
            }

            if(bannedPlayers != null) {
                yml.set("banned players", bannedPlayers.toString());
            }

            save();
        }
    }
}
