package me.randomhashtags.randomsky.addon.realm;

import me.randomhashtags.randomsky.addon.util.Identifiable;

import java.util.HashMap;
import java.util.List;

public interface RealmEvent extends Identifiable {
    HashMap<String, List<String>> getMessages();
}
