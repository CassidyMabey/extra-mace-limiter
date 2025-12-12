package com.notauthorised.extramacelimiter;

import com.notauthorised.extramacelimiter.events.*;
import com.notauthorised.extramacelimiter.util.VersionCompatibility;
import org.bukkit.plugin.java.JavaPlugin;

public class ExtraMaceLimiter extends JavaPlugin {

    private static ExtraMaceLimiter instance;

    @Override
    public void onEnable() {
        instance = this;
        
        // check version compatibility first
        if (!VersionCompatibility.isSupported()) {
            getLogger().severe("This server version may not be fully supported!");
            getLogger().severe("Supported versions: 1.18+ (1.21+ recommended for full features)");
        }
        
        VersionCompatibility.logVersionInfo(this);
        
        saveDefaultConfig();
        
        // register events
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerPickupListener(this), this);
        getServer().getPluginManager().registerEvents(new HopperListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemFrameListener(this), this);
        getServer().getPluginManager().registerEvents(new DecoratedPotListener(this), this);
        
        ExtraMaceLimiterCommand commandHandler = new ExtraMaceLimiterCommand(this);
        getCommand("extramacelimiter").setExecutor(commandHandler);
        getCommand("extramacelimiter").setTabCompleter(commandHandler);
        
        // startup message with version info
        getLogger().info("Extra Mace Limiter v" + getDescription().getVersion() + " has been enabled!");
        getLogger().info(VersionCompatibility.getCompatibilityInfo());
        
        // Warn about missing features if applicable
        if (!VersionCompatibility.hasMaces()) {
            getLogger().warning("Mace blocking features are disabled (requires Minecraft 1.21+)");
            getLogger().info("Plugin will still work for other blocked items and inventory management.");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Extra Mace Limiter has been disabled!");
    }
    
    public static ExtraMaceLimiter getInstance() {
        return instance;
    }
}