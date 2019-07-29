package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;

import java.util.List;

public interface PlayerSkillLevel extends Itemable {
    int getSlot();
    List<String> getPerk();
}
