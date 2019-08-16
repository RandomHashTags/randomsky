package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Identifiable;

import java.util.HashMap;
import java.util.List;

public interface RealmEvent extends Identifiable {
    HashMap<String, List<String>> getMessages();
}
