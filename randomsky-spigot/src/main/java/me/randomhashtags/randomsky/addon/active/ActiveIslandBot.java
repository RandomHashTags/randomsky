package me.randomhashtags.randomsky.addon.active;

import me.randomhashtags.randomsky.addon.bot.AutoBot;
import me.randomhashtags.randomsky.addon.bot.AutoBotUpgrade;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.UUID;

public class ActiveIslandBot {
    private UUID placer;
    private AutoBot type;
    private LivingEntity entity;
    private int level;
    private HashMap<AutoBotUpgrade, Integer> upgradeLevels;
    public ActiveIslandBot(UUID placer, AutoBot type, Location l, int level, HashMap<AutoBotUpgrade, Integer> upgradeLevels) {
        this.placer = placer;
        this.type = type;
        this.entity = entity;
        this.level = level;
        this.upgradeLevels = upgradeLevels;
    }
    public UUID getPlacer() { return placer; }
    public AutoBot getType() { return type; }
    public LivingEntity getEntity() { return entity; }
    public int getLevel() { return level; }
    public HashMap<AutoBotUpgrade, Integer> getUpgrades() { return upgradeLevels; }
}
