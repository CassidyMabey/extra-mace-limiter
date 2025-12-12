package com.notauthorised.extramacelimiter.events;

import com.notauthorised.extramacelimiter.ExtraMaceLimiter;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class ItemFrameListener implements Listener {
    
    private final ExtraMaceLimiter plugin;
    private final Map<UUID, Integer> playerAttempts = new HashMap<>();

    public ItemFrameListener(ExtraMaceLimiter plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!plugin.getConfig().getBoolean("block-item-frame-placement", true)) {
            return;
        }
        
        // check if its an item frame
        if (event.getRightClicked() instanceof ItemFrame) {
            Player player = event.getPlayer();
            ItemStack heldItem = player.getInventory().getItemInMainHand();
            
            if (EventUtils.containsBlockedItems(plugin, heldItem)) {
                event.setCancelled(true);
                EventUtils.handleSpecialItemBlockMessage(plugin, player, playerAttempts);
            }
        }
    }
}