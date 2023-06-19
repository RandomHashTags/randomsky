package me.randomhashtags.randomsky.addon;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.util.Mathable;
import me.randomhashtags.randomsky.util.FileRSPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.util.HashMap;

public interface EventAttribute extends Cancellable, Identifiable, Mathable {
    void load();
    void unload();
    void execute(@NotNull Event event);
    void execute(@NotNull Event event, @NotNull String value);
    void execute(@NotNull Event event, @NotNull String value, @NotNull HashMap<String, String> valueReplacements);
    void execute(@NotNull Event event, @NotNull HashMap<String, Entity> entities, @NotNull String value, @NotNull HashMap<String, String> valueReplacements);
    void execute(@NotNull Entity entity1, @NotNull Entity entity2, @NotNull String value);
    void execute(@NotNull Event event, @NotNull HashMap<Entity, String> recipientValues);
    void execute(@NotNull Event event, @NotNull HashMap<String, Entity> entities, @NotNull HashMap<Entity, String> recipientValues);
    void execute(@NotNull Event event, @NotNull HashMap<String, Entity> entities, @NotNull HashMap<Entity, String> recipientValues, @NotNull HashMap<String, String> valueReplacements);
    void executeAt(@NotNull HashMap<Location, String> locations);
    void executeData(@NotNull HashMap<FileRSPlayer, String> recipientValues, @NotNull HashMap<String, String> valueReplacements);
    void executeData(@NotNull HashMap<String, Entity> entities, @NotNull HashMap<FileRSPlayer, String> recipientValues, @NotNull HashMap<String, String> valueReplacements);
}
