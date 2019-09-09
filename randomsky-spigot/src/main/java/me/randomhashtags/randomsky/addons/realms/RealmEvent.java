package me.randomhashtags.randomsky.addons.realms;

import me.randomhashtags.randomsky.addons.util.Identifiable;

import java.util.HashMap;
import java.util.List;

public interface RealmEvent extends Identifiable {
    HashMap<String, List<String>> getMessages();
}
