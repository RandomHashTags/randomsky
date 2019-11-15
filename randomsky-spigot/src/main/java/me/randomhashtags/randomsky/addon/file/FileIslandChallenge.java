package me.randomhashtags.randomsky.addon.file;

import me.randomhashtags.randomsky.addon.island.IslandChallenge;
import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSAddon;
import me.randomhashtags.randomsky.util.RSStorage;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

public class FileIslandChallenge extends RSAddon implements IslandChallenge {
    private String name;
    private IslandChallenge required;
    private BigDecimal completion;
    private List<String> obj, attributes, rewards;

    public FileIslandChallenge(File f) {
        load(f);
        RSStorage.register(Feature.ISLAND_CHALLENGE, this);
    }

    public String getIdentifier() { return getYamlName(); }

    public int getSlot() { return yml.getInt("settings.slot"); }
    public String getName() {
        if(name == null) name = yml.getString("settings.name");
        return name;
    }
    public IslandChallenge getRequired() {
        if(required == null && yml.get("settings.requires completed challenge") != null) {
            final Identifiable i = RSStorage.get(Feature.ISLAND_CHALLENGE, yml.getString("settings.requires completed challenge"));
            if(i != null) {
                required = (IslandChallenge) i;
            }
        }
        return required;
    }
    public BigDecimal getCompletion() {
        if(completion == null) completion = BigDecimal.valueOf(yml.getDouble("settings.completion"));
        return completion;
    }
    public List<String> getObjective() {
        if(obj == null) obj = colorizeListString(yml.getStringList("objective"));
        return obj;
    }
    public List<String> getAttributes() {
        if(attributes == null) attributes = yml.getStringList("attributes");
        return attributes;
    }
    public List<String> getRewards() {
        if(rewards == null) rewards = yml.getStringList("rewards");
        return rewards;
    }
}
