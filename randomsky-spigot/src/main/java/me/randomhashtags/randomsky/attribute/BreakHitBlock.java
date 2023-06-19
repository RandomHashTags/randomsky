package me.randomhashtags.randomsky.attribute;

import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class BreakHitBlock extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull Event event) {
        if(event instanceof PlayerInteractEvent) {
            final PlayerInteractEvent e = (PlayerInteractEvent) event;
            final Block b = e.getClickedBlock();
            if(b != null) {
                b.breakNaturally();
            }
        }
    }
}
