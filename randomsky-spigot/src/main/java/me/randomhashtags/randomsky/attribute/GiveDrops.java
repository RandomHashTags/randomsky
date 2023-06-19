package me.randomhashtags.randomsky.attribute;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class GiveDrops extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull Event event, String value) {
        final String[] values = value.split(":");
        final int l = values.length;
        if(Boolean.parseBoolean(values[0])) {
            final double multiplier = l >= 2 ? evaluate(values[1]) : 1;
            final boolean smelt = l >= 3 && Boolean.parseBoolean(values[2]);
            if(event instanceof BlockBreakEvent) {
                final BlockBreakEvent e = (BlockBreakEvent) event;
                final Player player = e.getPlayer();
                final Block b = e.getBlock();
                final Collection<ItemStack> drops = b.getDrops();
                for(ItemStack i : drops) {
                    giveItem(player, i);
                }
                e.setCancelled(true);
                b.setType(Material.AIR);
            }
        }
    }
}
