package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.util.Itemable;

import java.util.List;

public interface PlayerSkillLevel extends Itemable {
    int getSlot();
    List<String> getPerk();
}
