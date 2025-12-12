package com.notauthorised.extramacelimiter.events;

import com.notauthorised.extramacelimiter.ExtraMaceLimiter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public class InventoryClickListener implements Listener {
    
    private final ExtraMaceLimiter plugin;
    private final Map<UUID, Integer> playerAttempts = new HashMap<>();

    public InventoryClickListener(ExtraMaceLimiter plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();
        Set<InventoryType> blockedInventories = EventUtils.getBlockedInventories(plugin);

        // Debug logging for inventory types
        if (plugin.getConfig().getBoolean("debug", false)) {
            player.sendMessage("§7[DEBUG] Inventory click - Type: " + inv.getType() + 
                             ", Blocked: " + blockedInventories.contains(inv.getType()) + 
                             ", Holder: " + (inv.getHolder() != null ? inv.getHolder().getClass().getSimpleName() : "null"));
        }

        // check the inventory type
        if (blockedInventories.contains(inv.getType())) {
            boolean shouldBlock = false;
            
            // handle clicking the storage inv
            if (event.getRawSlot() < inv.getSize()) {
                ItemStack holding = event.getCursor();

                // check if its a blocked item or bundle with blocked items
                if (EventUtils.containsBlockedItems(plugin, holding)) {
                    shouldBlock = true;
                }
                
                // the hotkey swapping
                if (event.getHotbarButton() != -1) {
                    ItemStack hotbarItem = player.getInventory().getItem(event.getHotbarButton());
                    if (EventUtils.containsBlockedItems(plugin, hotbarItem)) {
                        shouldBlock = true;
                    }
                }
            }
            // shift clicking
            else if (event.isShiftClick()) {
                ItemStack clicked = event.getCurrentItem();
                
                if (EventUtils.containsBlockedItems(plugin, clicked)) {
                    shouldBlock = true;
                }
            }
            
            // cancelling event
            if (shouldBlock) {
                event.setCancelled(true);
                EventUtils.handleSpecialItemBlockMessage(plugin, player, playerAttempts);
            }
        }
    }
}