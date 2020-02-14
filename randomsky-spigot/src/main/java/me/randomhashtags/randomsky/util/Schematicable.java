package me.randomhashtags.randomsky.util;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.world.registry.WorldData;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public interface Schematicable {
    default void cleanChunk(Chunk chunk) throws IOException {
        final File schematic = new File(worldeditF, "AIR_CHUNK.schematic");
        final Location b = chunk.getBlock(0, 0, 0).getLocation();
        final com.sk89q.worldedit.Vector to = new com.sk89q.worldedit.Vector(b.getBlockX(), b.getBlockY(), b.getBlockZ());
        com.sk89q.worldedit.world.World W = new BukkitWorld(chunk.getWorld());
        final WorldData worldData = W.getWorldData();
        final Clipboard clipboard = ClipboardFormat.SCHEMATIC.getReader(new FileInputStream(schematic)).read(worldData);
        final Schematic s = new Schematic(clipboard);
        s.paste(W, to, false, true, null);
    }
    default void pasteSchematic(File schematic, Location l) throws IOException {
        final com.sk89q.worldedit.Vector to = new com.sk89q.worldedit.Vector(l.getBlockX(), l.getBlockY(), l.getBlockZ());
        final com.sk89q.worldedit.world.World W = new BukkitWorld(l.getWorld());
        final WorldData worldData = W.getWorldData();
        final Clipboard clipboard = ClipboardFormat.SCHEMATIC.getReader(new FileInputStream(schematic)).read(worldData);
        final Schematic s = new Schematic(clipboard);
        s.paste(W, to, false, true, null);
    }
}
