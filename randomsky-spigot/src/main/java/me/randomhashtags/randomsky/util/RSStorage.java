package me.randomhashtags.randomsky.util;

import me.randomhashtags.randomsky.addon.util.Identifiable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class RSStorage {
    private static HashMap<Feature, HashMap<String, Identifiable>> FEATURES = new HashMap<>();

    public static void register(Feature f, @NotNull Identifiable obj) {
        if(!FEATURES.containsKey(f)) {
            FEATURES.put(f, new HashMap<>());
        }
        FEATURES.get(f).put(obj.getIdentifier(), obj);
    }
    public static void unregister(Feature f, @NotNull Identifiable obj) {
        if(FEATURES.containsKey(f)) {
            FEATURES.get(f).remove(obj.getIdentifier());
        }
    }

    public static Identifiable get(Feature f, @NotNull String identifier) {
        return FEATURES.containsKey(f) ? FEATURES.get(f).getOrDefault(identifier, null) : null;
    }
    public static List<Identifiable> getAll(Feature f) {
        return FEATURES.containsKey(f) ? new ArrayList<>(FEATURES.get(f).values()) : new ArrayList<>();
    }
    public static void unregisterAll(Feature...features) {
        for(Feature f : features) {
            FEATURES.remove(f);
        }
    }
}
