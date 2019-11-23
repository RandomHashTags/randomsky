package me.randomhashtags.randomsky.addon;

import java.util.Set;

public interface ChatChannels {
    ChatChannel getCurrent();
    Set<ChatChannel> getActive();
    default boolean isActive(ChatChannel channel) {
        return channel == getCurrent() || getActive().contains(channel);
    }
    default boolean canReceiveMessage(ChatChannel fromChannel) {
        return isActive(fromChannel);
    }
}
