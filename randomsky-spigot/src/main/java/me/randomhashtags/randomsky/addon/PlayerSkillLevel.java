package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.Itemable;

import java.util.List;

public interface PlayerSkillLevel extends Itemable {
    int getSlot();
    List<String> getPerk();
}
