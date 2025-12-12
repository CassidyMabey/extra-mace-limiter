package com.notauthorised.extramacelimiter.events;

import com.notauthorised.extramacelimiter.ExtraMaceLimiter;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.Set;


public class HopperListener implements Listener {
    
    private final ExtraMaceLimiter plugin;

    public HopperListener(ExtraMaceLimiter plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryPickupItem(InventoryPickupItemEvent event) {
        if (!plugin.getConfig().getBoolean("block-hopper-pickup", true) && 
            !plugin.getConfig().getBoolean("block-hopper-minecart-pickup", true)) {
            return;
        }
        
        // check if pickup item is blocked
        if (EventUtils.isBlockedItem(plugin, event.getItem().getItemStack())) {
            Inventory inventory = event.getInventory();
            
            // hopper cancelling
            if (inventory.getType() == InventoryType.HOPPER && 
                plugin.getConfig().getBoolean("block-hopper-pickup", true)) {
                event.setCancelled(true);
                return;
            }
            
            // hopper minecart cancelling
            if (inventory.getHolder() instanceof HopperMinecart && 
                plugin.getConfig().getBoolean("block-hopper-minecart-pickup", true)) {
                event.setCancelled(true);
                return;
            }
        }
    }
    
    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (EventUtils.isBlockedItem(plugin, event.getItem())) {
            Inventory destination = event.getDestination();
            Set<InventoryType> blockedInventories = EventUtils.getBlockedInventories(plugin);
            
            // check destination type
            if (blockedInventories.contains(destination.getType())) {
                event.setCancelled(true);
                return;
            }
            
            // added check for hoppers
            if (destination.getHolder() instanceof HopperMinecart && 
                plugin.getConfig().getBoolean("block-hopper-minecart-pickup", true)) {
                event.setCancelled(true);
                return;
            }
        }
    }
}