package me.randomhashtags.randomsky.addons.alliance;

import me.randomhashtags.randomsky.addons.util.Identifiable;

import java.util.List;

public interface AllianceRole extends Identifiable {
    String getTag();
    String getChatTag();
    String getColor();
    List<String> getGrantedPermissions();
}
