package com.notauthorised.extramacelimiter.events;

import com.notauthorised.extramacelimiter.ExtraMaceLimiter;
import com.notauthorised.extramacelimiter.util.VersionCompatibility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class EventUtils {
    
    public static Set<InventoryType> getBlockedInventories(ExtraMaceLimiter plugin) {
        Set<InventoryType> blocked = EnumSet.noneOf(InventoryType.class);
        
        if (plugin.getConfig().getBoolean("blocked-storages.chest", true)) {
            blocked.add(InventoryType.CHEST);
        }
        if (plugin.getConfig().getBoolean("blocked-storages.ender-chest", true)) {
            blocked.add(InventoryType.ENDER_CHEST);
        }
        if (plugin.getConfig().getBoolean("blocked-storages.barrel", true)) {
            blocked.add(InventoryType.BARREL);
        }
        if (plugin.getConfig().getBoolean("blocked-storages.shulker-box", true)) {
            blocked.add(InventoryType.SHULKER_BOX);
        }
        if (plugin.getConfig().getBoolean("blocked-storages.hopper", true)) {
            blocked.add(InventoryType.HOPPER);
        }
        if (plugin.getConfig().getBoolean("blocked-storages.dropper", true)) {
            blocked.add(InventoryType.DROPPER);
        }
        if (plugin.getConfig().getBoolean("blocked-storages.dispenser", true)) {
            blocked.add(InventoryType.DISPENSER);
        }
        if (plugin.getConfig().getBoolean("blocked-storages.furnace", true)) {
            blocked.add(InventoryType.FURNACE);
        }
        if (plugin.getConfig().getBoolean("blocked-storages.blast-furnace", true)) {
            blocked.add(InventoryType.BLAST_FURNACE);
        }
        if (plugin.getConfig().getBoolean("blocked-storages.smoker", true)) {
            blocked.add(InventoryType.SMOKER);
        }
        // Only add crafter if the version supports it
        if (VersionCompatibility.hasCrafter() && plugin.getConfig().getBoolean("blocked-storages.crafter", true)) {
            blocked.add(InventoryType.CRAFTER);
        }
        if (plugin.getConfig().getBoolean("blocked-storages.decorated-pot", true)) {
            blocked.add(InventoryType.DECORATED_POT);
        }
        
        // Debug logging for blocked inventories
        if (plugin.getConfig().getBoolean("debug", false)) {
            plugin.getLogger().info("[DEBUG] Blocked inventory types: " + blocked.toString());
        }
        
        return blocked;
    }

    public static boolean containsBlockedItems(ExtraMaceLimiter plugin, ItemStack item) {
        if (item == null) return false;
        
        // check if item is blocked 
        if (isBlockedItem(plugin, item)) {
            return true;
        }
        
        // check if in bundle containing blocked items (only if version supports bundles)
        if (VersionCompatibility.hasBundles() && 
            plugin.getConfig().getBoolean("block-bundles-with-maces", true) && 
            item.getType() == Material.BUNDLE && item.hasItemMeta()) {
            
            BundleMeta bundleMeta = (BundleMeta) item.getItemMeta();
            if (bundleMeta.hasItems()) {
                for (ItemStack bundledItem : bundleMeta.getItems()) {
                    if (bundledItem != null && isBlockedItem(plugin, bundledItem)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    public static boolean containsAdditionalBlockedItems(ExtraMaceLimiter plugin, ItemStack item) {
        if (item == null) return false;
        
        // check simple item blocking
        List<String> additionalItems = plugin.getConfig().getStringList("additional-blocked-items.items");
        if (additionalItems != null && !additionalItems.isEmpty()) {
            String itemType = item.getType().name();
            if (additionalItems.contains(itemType)) {
                return true;
            }
        }
        
        // custom model data blocking (only if version supports it)
        if (VersionCompatibility.hasCustomModelDataSupport() && 
            item.hasItemMeta() && item.getItemMeta().hasCustomModelData()) {
            int customModelData = item.getItemMeta().getCustomModelData();
            String materialName = item.getType().name();
            
            List<Integer> blockedCustomModelData = plugin.getConfig().getIntegerList(
                "additional-blocked-items.custom-model-data." + materialName);
            
            if (blockedCustomModelData != null && blockedCustomModelData.contains(customModelData)) {
                return true;
            }
        }
        
        return false;
    }

    public static boolean isBlockedItem(ExtraMaceLimiter plugin, ItemStack item) {
        if (item == null) return false;
        
        // check if it's a mace (only if version supports maces)
        if (VersionCompatibility.hasMaces() && item.getType() == Material.MACE) {
            return true;
        }
        
        // check for additional items
        return containsAdditionalBlockedItems(plugin, item);
    }

    public static void handleSpecialItemBlockMessage(ExtraMaceLimiter plugin, Player player, java.util.Map<UUID, Integer> playerAttempts) {
        UUID playerId = player.getUniqueId();
        int attempts = playerAttempts.getOrDefault(playerId, 0) + 1;
        playerAttempts.put(playerId, attempts);
        
        int frequency = plugin.getConfig().getInt("messages.storage-blocking.frequency", 5);
        
        if (attempts == 1 || attempts % frequency == 0) {
            String message = plugin.getConfig().getString("messages.storage-blocking.text", 
                "&c&lHey! &7You can't move a special item outside your inventory.");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public static void handleSpecialItemPickupMessage(ExtraMaceLimiter plugin, Player player, java.util.Map<UUID, Integer> playerPickupAttempts) {
        UUID playerId = player.getUniqueId();
        int attempts = playerPickupAttempts.getOrDefault(playerId, 0) + 1;
        playerPickupAttempts.put(playerId, attempts);
        
        int frequency = plugin.getConfig().getInt("messages.pickup-blocking.frequency", 100);
        
        if (attempts == 1 || attempts % frequency == 0) {
            String message = plugin.getConfig().getString("messages.pickup-blocking.text", 
                "&c&lHey! &7You can't pick up that special item.");
            
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }
        
}