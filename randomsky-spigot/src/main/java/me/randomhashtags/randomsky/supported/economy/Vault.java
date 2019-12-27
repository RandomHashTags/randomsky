package me.randomhashtags.randomsky.supported.economy;

import me.randomhashtags.randomsky.universal.UVersionable;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class Vault implements UVersionable {
    private static Vault instance;
    public static Vault getVault() {
        if(instance == null) instance = new Vault();
        return instance;
    }
    private Economy economy;
    private Chat chat;
    private Permission permissions;
    public Economy getEconomy() {
        if(economy == null) {
            final RegisteredServiceProvider<Economy> economyProvider = SERVICES_MANAGER.getRegistration(Economy.class);
            if(economyProvider != null) economy = economyProvider.getProvider();
        }
        return economy;
    }
    public Chat getChat() {
        if(chat == null) {
            final RegisteredServiceProvider<Chat> rsp = SERVICES_MANAGER.getRegistration(Chat.class);
            chat = rsp != null ? rsp.getProvider() : null;
        }
        return chat;
    }
    public Permission getPermission() {
        if(permissions == null) {
            final RegisteredServiceProvider<Permission> rsp = SERVICES_MANAGER.getRegistration(Permission.class);
            permissions = rsp != null ? rsp.getProvider() : null;
        }
        return permissions;
    }
}
