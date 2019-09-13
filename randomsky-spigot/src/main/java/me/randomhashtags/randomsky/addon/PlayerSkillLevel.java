package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.addon.util.Slotable;

import java.util.List;

public interface PlayerSkillLevel extends Itemable, Slotable {
    List<String> getPerk();
}
