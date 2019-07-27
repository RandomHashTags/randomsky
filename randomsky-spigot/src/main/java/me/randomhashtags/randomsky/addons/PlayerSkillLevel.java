package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;

import java.util.List;

public abstract class PlayerSkillLevel extends Itemable {
    public abstract int getSlot();
    public abstract List<String> getPerk();
}
