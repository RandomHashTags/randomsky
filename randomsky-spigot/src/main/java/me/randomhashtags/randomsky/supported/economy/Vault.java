package me.randomhashtags.randomsky.supported.economy;

import me.randomhashtags.randomsky.universal.UVersionable;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;

public enum Vault implements UVersionable {
    INSTANCE;
    private Economy economy;
    private Chat chat;
    private Permission permissions;

    @Nullable
    public Economy getEconomy() {
        if(economy == null) {
            final RegisteredServiceProvider<Economy> economyProvider = SERVICES_MANAGER.getRegistration(Economy.class);
            if(economyProvider != null) economy = economyProvider.getProvider();
        }
        return economy;
    }
    @Nullable
    public Chat getChat() {
        if(chat == null) {
            final RegisteredServiceProvider<Chat> rsp = SERVICES_MANAGER.getRegistration(Chat.class);
            chat = rsp != null ? rsp.getProvider() : null;
        }
        return chat;
    }
    @Nullable
    public Permission getPermission() {
        if(permissions == null) {
            final RegisteredServiceProvider<Permission> rsp = SERVICES_MANAGER.getRegistration(Permission.class);
            permissions = rsp != null ? rsp.getProvider() : null;
        }
        return permissions;
    }
}
