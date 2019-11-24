package me.randomhashtags.randomsky.addon.file;

import me.randomhashtags.randomsky.addon.alliance.AllianceRole;
import me.randomhashtags.randomsky.util.Feature;
import me.randomhashtags.randomsky.util.RSAddon;
import me.randomhashtags.randomsky.util.RSStorage;

import java.io.File;
import java.util.List;

public class FileAllianceRole extends RSAddon implements AllianceRole {
    private String tag, chattag, color;
    private List<String> grantedPermissions;
    public FileAllianceRole(File f) {
        load(f);
        RSStorage.register(Feature.ALLIANCE_ROLE, this);
    }

    public String getIdentifier() { return getYamlName(); }

    public String getTag() {
        if(tag == null) {
            tag = colorize(yml.getString("settings.tag"));
        }
        return tag;
    }
    public String getChatTag() {
        if(chattag == null) {
            chattag = colorize(yml.getString("settings.chat tag"));
        }
        return chattag;
    }
    public String getColor() {
        if(color == null) {
            color = colorize(yml.getString("settings.color"));
        }
        return color;
    }
    public List<String> getGrantedPermissions() {
        if(grantedPermissions == null) {
            grantedPermissions = yml.getStringList("granted permissions");
        }
        return grantedPermissions;
    }
}
