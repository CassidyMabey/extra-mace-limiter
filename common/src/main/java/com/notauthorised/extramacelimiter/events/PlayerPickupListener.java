package com.notauthorised.extramacelimiter.events;

import com.notauthorised.extramacelimiter.ExtraMaceLimiter;
import com.notauthorised.extramacelimiter.util.VersionCompatibility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class PlayerPickupListener implements Listener {
    
    private final ExtraMaceLimiter plugin;
    private final Map<UUID, Integer> playerPickupAttempts = new HashMap<>();

    public PlayerPickupListener(ExtraMaceLimiter plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (!plugin.getConfig().getBoolean("stop-pickup-at-max-maces", true)) {
            return;
        }
        
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem().getItemStack();
        
        // Check if the item being picked up is a special item (mace or blocked item)
        boolean isMace = VersionCompatibility.hasMaces() && itemStack.getType() == Material.MACE;
        boolean isBlockedItem = EventUtils.isBlockedItem(plugin, itemStack);
        
        // Debug logging
        if (plugin.getConfig().getBoolean("debug", false)) {
            player.sendMessage("§7[DEBUG] Pickup attempt - Item: " + itemStack.getType() + 
                             ", IsMace: " + isMace + ", IsBlocked: " + isBlockedItem + 
                             (itemStack.hasItemMeta() && itemStack.getItemMeta().hasCustomModelData() ? 
                             ", CustomModelData: " + itemStack.getItemMeta().getCustomModelData() : ", No CMD"));
        }
        
        if (isMace || isBlockedItem) {
            int maxSpecialItems = plugin.getConfig().getInt("max-maces-in-inventory", 2);
            
            if (maxSpecialItems == -1) {
                return;
            }
            
            int specialItemCount = countSpecialItemsInInventory(player);
            
            // Debug logging
            if (plugin.getConfig().getBoolean("debug", false)) {
                player.sendMessage("§7[DEBUG] Special item count: " + specialItemCount + "/" + maxSpecialItems);
            }
            
            if (specialItemCount >= maxSpecialItems) {
                event.setCancelled(true);
                handleSpecialItemPickupBlockMessage(player, isMace);
            }
        }
    }

    private int countSpecialItemsInInventory(Player player) {
        int count = 0;
        ItemStack[] contents = player.getInventory().getContents();
        
        for (ItemStack item : contents) {
            if (item != null) {
                // Count maces if version supports them
                if (VersionCompatibility.hasMaces() && item.getType() == Material.MACE) {
                    count += item.getAmount();
                }
                // Count other blocked items (including custom model data items)
                else if (EventUtils.isBlockedItem(plugin, item)) {
                    count += item.getAmount();
                }
            }
        }
        
        return count;
    }

    private void handleSpecialItemPickupBlockMessage(Player player, boolean isMace) {
        UUID playerId = player.getUniqueId();
        int attempts = playerPickupAttempts.getOrDefault(playerId, 0) + 1;
        playerPickupAttempts.put(playerId, attempts);
        
        int frequency = plugin.getConfig().getInt("messages.pickup-blocking.frequency", 100);
        
        if (attempts == 1 || attempts % frequency == 0) {
            String message;
            if (isMace) {
                message = plugin.getConfig().getString("messages.pickup-blocking.text", 
                    "&c&lHey! &7You can't carry more than 2 special items at once.");
            } else {
                message = plugin.getConfig().getString("messages.pickup-blocking.text", 
                    "&c&lHey! &7You can't carry more than 2 special items at once.")
                    .replace("mace", "special item");
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }
}