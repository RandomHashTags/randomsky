package me.randomhashtags.randomsky.addon.obj;

import me.randomhashtags.randomsky.addon.ChatChannel;
import me.randomhashtags.randomsky.addon.ChatChannels;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ChatChannelsObj implements ChatChannels {
    private ChatChannel current;
    private Set<ChatChannel> active;
    private static HashSet<ChatChannel> DEFAULT = new HashSet<>(Arrays.asList(ChatChannel.ISLAND, ChatChannel.ALLIANCE, ChatChannel.ALLY, ChatChannel.TRUCE, ChatChannel.LOCAL, ChatChannel.ADVERTISING));
    public ChatChannelsObj() {
        this(ChatChannel.GLOBAL, DEFAULT);
    }
    public ChatChannelsObj(ChatChannel current, Set<ChatChannel> active) {
        this.current = current;
        this.active = active;
    }
    public ChatChannel getCurrent() { return current; }
    public Set<ChatChannel> getActive() {
        return active;
    }
}
