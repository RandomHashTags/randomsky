package me.randomhashtags.randomsky.util;

public enum ToggleType {
    TPA_REQUESTS,
    PRIVATE_MESSAGE,
    FILTER_CHAT(false),
    ISLAND_INVITE_NOTIFICATIONS,
    PAY_REQUESTS,
    MEMBER_VISITING,
    PUNCH_TO_KICK,
    INSTANT_BLOCK_BREAK,
    INSTANT_BLOCK_PICKUP,
    CLEAR_INVENTORY_CONFIRMATION,
    AUCTION_BUY_CONFIRM,
    AUCTION_SELL_CONFIRM,
    COIN_FLIP_NOTIFICATIONS,
    BLEED_NOTIFICATIONS,
    ENCHANT_DEBUG,
    BREAK_PARTICLES,
    INSTA_BREAK_TUTORIAL,
    JACKPOT_COUNTDOWN;
    private boolean defaultValue;
    ToggleType() {
        defaultValue = true;
    }
    ToggleType(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }
    public boolean getDefaultValue() { return defaultValue; }
}
