package me.randomhashtags.randomsky.attribute;

import me.randomhashtags.randomsky.addon.EventAttribute;
import me.randomhashtags.randomsky.attributesys.EventReplacer;
import me.randomhashtags.randomsky.universal.UVersionable;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.FileRSPlayer;
import me.randomhashtags.randomsky.util.RSStorage;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class AbstractEventAttribute implements UVersionable, EventAttribute, EventReplacer {
    private boolean cancelled;
    public String getIdentifier() {
        final String[] n = getClass().getName().split("\\.");
        return n[n.length-1].toUpperCase();
    }
    public void load() {
        RSStorage.register(Feature.EVENT_ATTRIBUTE, this);
    }
    public void unload() {
        RSStorage.unregister(Feature.EVENT_ATTRIBUTE, this);
    }

    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    public void execute(@NotNull Event event) {}
    public void execute(@NotNull Event event, String value) {}
    public void execute(@NotNull Event event, String value, HashMap<String, String> valueReplacements) {}
    public void execute(@NotNull Event event, HashMap<String, Entity> entities, String value, HashMap<String, String> valueReplacements) {}
    public void execute(String value) {}
    public void execute(@NotNull Entity entity1, Entity entity2, String value) {}
    public void execute(@NotNull Event event, HashMap<Entity, String> recipientValues) {}
    public void execute(@NotNull Event event, HashMap<String, Entity> entities, HashMap<Entity, String> recipientValues) {}
    public void execute(@NotNull Event event, HashMap<String, Entity> entities, HashMap<Entity, String> recipientValues, HashMap<String, String> valueReplacements) {}
    public void executeAt(HashMap<Location, String> locations) {}
    public void executeData(HashMap<FileRSPlayer, String> recipientValues, HashMap<String, String> valueReplacements) {}
    public void executeData(HashMap<String, Entity> entities, HashMap<FileRSPlayer, String> recipientValues, HashMap<String, String> valueReplacements) {}
}
