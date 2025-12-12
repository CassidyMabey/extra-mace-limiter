package com.notauthorised.extramacelimiter.events;

import com.notauthorised.extramacelimiter.ExtraMaceLimiter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DecoratedPotListener implements Listener {
    
    private final ExtraMaceLimiter plugin;
    private final Map<UUID, Integer> playerAttempts = new HashMap<>();

    public DecoratedPotListener(ExtraMaceLimiter plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!plugin.getConfig().getBoolean("blocked-storages.decorated-pot", true)) {
            return;
        }
        
        // check if its a right click on a decorated pot
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.DECORATED_POT) {
            return;
        }
        
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        
        if (plugin.getConfig().getBoolean("debug", false)) {
            player.sendMessage("§7[DEBUG] Right-clicking decorated pot with: " + 
                             (heldItem.getType() != Material.AIR ? heldItem.getType() + 
                             (heldItem.hasItemMeta() && heldItem.getItemMeta().hasCustomModelData() ? 
                             " (CMD: " + heldItem.getItemMeta().getCustomModelData() + ")" : "") : "empty hand"));
        }
        
        // check if trying to put special item in pot
        if (heldItem.getType() != Material.AIR && EventUtils.containsBlockedItems(plugin, heldItem)) {
            event.setCancelled(true);
            EventUtils.handleSpecialItemBlockMessage(plugin, player, playerAttempts);
            
            if (plugin.getConfig().getBoolean("debug", false)) {
                player.sendMessage("§7[DEBUG] Blocked decorated pot interaction with special item: " + heldItem.getType());
            }
        }
    }
}