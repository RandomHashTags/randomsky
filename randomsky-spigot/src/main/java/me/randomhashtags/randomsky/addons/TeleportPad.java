package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;
import me.randomhashtags.randomsky.addons.utils.RequiredIslandLevel;

import java.util.List;

public interface TeleportPad extends Itemable, RequiredIslandLevel {
    List<String> getLinkingInitiatedMsg();
    List<String> geLinkedMsg();
    List<String> getLinkedNoLongetAvailableMsg();
    List<String> getInfoMsg();
    List<String> getItemFilterTypeMsg();
    List<String> getMaterialListUpdatedMsg();
}
