package com.notauthorised.extramacelimiter.util;

import org.bukkit.Bukkit;


public class VersionCompatibility {
    
    private static final String SERVER_VERSION = Bukkit.getBukkitVersion();
    private static final String NMS_VERSION = getNMSVersionSafe();
    
    // version detection
    private static final boolean IS_1_18 = SERVER_VERSION.contains("1.18");
    private static final boolean IS_1_19 = SERVER_VERSION.contains("1.19");
    private static final boolean IS_1_20 = SERVER_VERSION.contains("1.20");
    private static final boolean IS_1_21 = SERVER_VERSION.contains("1.21");
    private static final boolean IS_1_22 = SERVER_VERSION.contains("1.22");
    
    // platform detection
    private static final boolean IS_PAPER = checkPaper();
    private static final boolean IS_PURPUR = checkPurpur();
    private static final boolean IS_PUFFERFISH = checkPufferfish();
    private static final boolean IS_SPIGOT = checkSpigot();
    private static final boolean IS_FOLIA = checkFolia();
    
    // feature availability
    private static final boolean HAS_MACES = IS_1_21 || IS_1_22; // Maces were added in 1.21
    private static final boolean HAS_BUNDLES = IS_1_20 || IS_1_21 || IS_1_22; // Bundles exist in 1.20+
    private static final boolean HAS_CRAFTER = IS_1_21 || IS_1_22; // Auto crafters added in 1.21
    
    
    private static String getNMSVersionSafe() {
        try {
            String[] parts = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
            if (parts.length > 3) {
                return parts[3];
            } else {
                // modern paper doesnt use versioned packages
                return "modern";
            }
        } catch (Exception e) {
            return "unknown";
        }
    }
    
    // version getters
    public static String getServerVersion() {
        return SERVER_VERSION;
    }
    
    public static String getNMSVersion() {
        return NMS_VERSION;
    }
    
    // version checks
    public static boolean is118() {
        return IS_1_18;
    }
    
    public static boolean is119() {
        return IS_1_19;
    }
    
    public static boolean is120() {
        return IS_1_20;
    }
    
    public static boolean is121() {
        return IS_1_21;
    }
    
    public static boolean is122() {
        return IS_1_22;
    }
    
    public static boolean isModern() {
        return IS_1_20 || IS_1_21 || IS_1_22;
    }
    
    public static boolean isLegacy() {
        return IS_1_18 || IS_1_19;
    }
    
    public static boolean isSupported() {
        // plugin supports 1.18+
        return !isLegacy() || (isLegacy() && hasBasicSupport());
    }
    
    private static boolean hasBasicSupport() {
        // even legacy versions can use basic inventory blocking
        return true;
    }
    
    // platform detection methods
    private static boolean checkPaper() {
        return hasClass("io.papermc.paper.configuration.Configuration") ||
               hasClass("com.destroystokyo.paper.PaperConfig");
    }
    
    private static boolean checkPurpur() {
        return hasClass("org.purpurmc.purpur.PurpurConfig");
    }
    
    private static boolean checkPufferfish() {
        return hasClass("gg.pufferfish.pufferfish.PufferfishConfig");
    }
    
    private static boolean checkSpigot() {
        return hasClass("org.spigotmc.SpigotConfig") && !checkPaper();
    }
    
    private static boolean checkFolia() {
        return hasClass("io.papermc.paper.threadedregions.RegionizedServer");
    }
    
    // platform getters
    public static boolean isPaper() {
        return IS_PAPER;
    }
    
    public static boolean isPurpur() {
        return IS_PURPUR;
    }
    
    public static boolean isPufferfish() {
        return IS_PUFFERFISH;
    }
    
    public static boolean isSpigot() {
        return IS_SPIGOT;
    }
    
    public static boolean isFolia() {
        return IS_FOLIA;
    }
    
    public static String getPlatform() {
        if (IS_FOLIA) return "Folia";
        if (IS_PURPUR) return "Purpur";
        if (IS_PUFFERFISH) return "Pufferfish";
        if (IS_PAPER) return "Paper";
        if (IS_SPIGOT) return "Spigot";
        return "Unknown";
    }
    
    // feature availability
    public static boolean hasMaces() {
        return HAS_MACES;
    }
    
    public static boolean hasBundles() {
        return HAS_BUNDLES;
    }
    
    public static boolean hasCrafter() {
        return HAS_CRAFTER;
    }
    
    public static boolean supportsAdvancedFeatures() {
        return IS_PAPER || IS_PURPUR || IS_PUFFERFISH || IS_FOLIA;
    }
    
    public static boolean hasCustomModelDataSupport() {
        // custom model data added in 1.14
        return true;
    }
    
    public static boolean supportsInventoryFeatures() {
        // all supported versions have inventory management
        return true;
    }
    
    // compatibility info
    public static String getCompatibilityInfo() {
        StringBuilder info = new StringBuilder();
        info.append(String.format("Running %s %s", getPlatform(), getServerVersion()));
        
        if (!hasMaces()) {
            info.append(" (Mace features disabled - requires 1.21+)");
        }
        if (!hasCrafter()) {
            info.append(" (Crafter blocking disabled - requires 1.21+)");
        }
        
        return info.toString();
    }
    
    public static String getFeatureSupport() {
        StringBuilder features = new StringBuilder();
        features.append("Features: ");
        
        if (hasMaces()) features.append("Maces ✓ ");
        else features.append("Maces ✗ ");
        
        if (hasBundles()) features.append("Bundles ✓ ");
        else features.append("Bundles ✗ ");
        
        if (hasCrafter()) features.append("Crafter ✓ ");
        else features.append("Crafter ✗ ");
        
        if (hasCustomModelDataSupport()) features.append("CustomModelData ✓ ");
        else features.append("CustomModelData ✗ ");
        
        if (supportsAdvancedFeatures()) features.append("Advanced ✓");
        else features.append("Advanced ✗");
        
        return features.toString();
    }
    
    // utility methods
    private static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    public static void logVersionInfo(org.bukkit.plugin.Plugin plugin) {
        plugin.getLogger().info("=== ExtraMaceLimiter Compatibility Info ===");
        plugin.getLogger().info("Platform: " + getPlatform());
        plugin.getLogger().info("Version: " + getServerVersion());
        plugin.getLogger().info("NMS: " + getNMSVersion());
        plugin.getLogger().info(getFeatureSupport());
        plugin.getLogger().info("Advanced Features: " + (supportsAdvancedFeatures() ? "Enabled" : "Limited"));
        
        if (!isSupported()) {
            plugin.getLogger().warning("This version may have limited functionality!");
        }
        if (!hasMaces()) {
            plugin.getLogger().warning("Maces not available - upgrade to 1.21+ for full functionality");
        }
        
        plugin.getLogger().info("========================================");
    }
    

    public static boolean isAsync() {
        if (isFolia()) {
            try {
                // on folia check if your on the main thread
                return !Bukkit.isPrimaryThread();
            } catch (Exception e) {
                return false;
            }
        }
        return !Bukkit.isPrimaryThread();
    }
    
    public static String getSchedulerType() {
        if (isFolia()) return "Folia RegionScheduler";
        if (supportsAdvancedFeatures()) return "BukkitScheduler (Enhanced)";
        return "BukkitScheduler (Basic)";
    }
}