package me.randomhashtags.randomsky.event.island;

import me.randomhashtags.randomsky.addon.island.Island;
import me.randomhashtags.randomsky.event.RSEventCancellable;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class IslandPlaceBlockEvent extends RSEventCancellable {
    private BlockPlaceEvent event;
    private ItemStack is;
    private Island island;
    public IslandPlaceBlockEvent(BlockPlaceEvent event, ItemStack is, Island island) {
        super(event.getPlayer());
        this.event = event;
        this.is = is;
        this.island = island;
    }
    public BlockPlaceEvent getEvent() { return event; }
    public ItemStack getItem() { return is; }
    public Island getIsland() { return island; }
}
