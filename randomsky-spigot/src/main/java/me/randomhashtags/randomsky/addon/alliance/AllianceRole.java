package me.randomhashtags.randomsky.addon.alliance;

import me.randomhashtags.randomsky.addon.util.Identifiable;

import java.util.List;

public interface AllianceRole extends Identifiable {
    String getTag();
    String getChatTag();
    String getColor();
    List<String> getGrantedPermissions();
}
