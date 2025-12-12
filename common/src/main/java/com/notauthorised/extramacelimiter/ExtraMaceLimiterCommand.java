package com.notauthorised.extramacelimiter;

import com.notauthorised.extramacelimiter.util.VersionCompatibility;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExtraMaceLimiterCommand implements CommandExecutor, TabCompleter {

    private final ExtraMaceLimiter plugin;

    public ExtraMaceLimiterCommand(ExtraMaceLimiter plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // permission check
        if (!sender.hasPermission("extramacelimiter.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        // help incase of no args
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "reload":
                handleReload(sender);
                break;
            case "version":
                handleVersion(sender);
                break;
            case "author":
                handleAuthor(sender);
                break;
            case "help":
                showHelp(sender);
                break;
            case "identify":
                handleIdentify(sender);
                break;
            case "compatibility":
            case "compat":
                handleCompatibility(sender);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand: " + args[0]);
                sender.sendMessage(ChatColor.GRAY + "Use " + ChatColor.WHITE + "/" + label + " help" + ChatColor.GRAY + " for available commands.");
                break;
        }

        return true;
    }

    private void handleReload(CommandSender sender) {
        try {
            // reload config
            plugin.reloadConfig();
            
            // send successful restart
            sender.sendMessage(ChatColor.GREEN + "Extra Mace Limiter configuration reloaded successfully!");
            
            // updated config (remove later)
            int maxMaces = plugin.getConfig().getInt("max-maces-in-inventory", 2);
            String maxMacesDisplay = maxMaces == -1 ? "unlimited" : String.valueOf(maxMaces);
            
            sender.sendMessage(ChatColor.GRAY + "Max maces per player: " + ChatColor.WHITE + maxMacesDisplay);
            sender.sendMessage(ChatColor.GRAY + "Blocked storage types: " + ChatColor.WHITE + getBlockedStorageCount() + "/11");
            sender.sendMessage(ChatColor.GRAY + "Hopper pickup blocking: " + ChatColor.WHITE + 
                (plugin.getConfig().getBoolean("block-hopper-pickup", true) ? "enabled" : "disabled"));
            sender.sendMessage(ChatColor.GRAY + "Item frame blocking: " + ChatColor.WHITE + 
                (plugin.getConfig().getBoolean("block-item-frame-placement", true) ? "enabled" : "disabled"));
            
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Error reloading configuration: " + e.getMessage());
            plugin.getLogger().severe("Error reloading configuration: " + e.getMessage());
        }
    }

    private void handleVersion(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Extra Mace Limiter ===");
        sender.sendMessage(ChatColor.YELLOW + "Plugin Version: " + ChatColor.WHITE + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.YELLOW + "API Version: " + ChatColor.WHITE + plugin.getDescription().getAPIVersion());
        sender.sendMessage(ChatColor.YELLOW + "Server: " + ChatColor.WHITE + VersionCompatibility.getPlatform() + " " + VersionCompatibility.getServerVersion());
        sender.sendMessage(ChatColor.YELLOW + "Java: " + ChatColor.WHITE + System.getProperty("java.version"));
        sender.sendMessage(ChatColor.YELLOW + "Description: " + ChatColor.WHITE + plugin.getDescription().getDescription());
        
        // show compatibility status
        if (VersionCompatibility.hasMaces()) {
            sender.sendMessage(ChatColor.GREEN + "✓ Mace features available");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "⚠ Mace features disabled (upgrade to 1.21+)");
        }
        
        sender.sendMessage(ChatColor.GOLD + "Use /eml compatibility for detailed compatibility info");
        sender.sendMessage(ChatColor.GOLD + "=========================");
    }

    private void handleCompatibility(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Compatibility Information ===");
        sender.sendMessage(ChatColor.YELLOW + "Platform: " + ChatColor.WHITE + VersionCompatibility.getPlatform());
        sender.sendMessage(ChatColor.YELLOW + "Server Version: " + ChatColor.WHITE + VersionCompatibility.getServerVersion());
        sender.sendMessage(ChatColor.YELLOW + "NMS Version: " + ChatColor.WHITE + VersionCompatibility.getNMSVersion());
        sender.sendMessage(ChatColor.YELLOW + "Java Version: " + ChatColor.WHITE + System.getProperty("java.version"));
        
        sender.sendMessage(ChatColor.GOLD + "\n=== Feature Support ===");
        
        // Mace support
        if (VersionCompatibility.hasMaces()) {
            sender.sendMessage(ChatColor.GREEN + "✓ Maces: Fully supported");
        } else {
            sender.sendMessage(ChatColor.RED + "✗ Maces: Not available (requires 1.21+)");
        }
        
        // Bundle support
        if (VersionCompatibility.hasBundles()) {
            sender.sendMessage(ChatColor.GREEN + "✓ Bundles: Supported");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "⚠ Bundles: Limited (requires 1.20+)");
        }
        
        // Crafter support
        if (VersionCompatibility.hasCrafter()) {
            sender.sendMessage(ChatColor.GREEN + "✓ Auto Crafter: Supported");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "⚠ Auto Crafter: Not available (requires 1.21+)");
        }
        
        // Custom model data
        if (VersionCompatibility.hasCustomModelDataSupport()) {
            sender.sendMessage(ChatColor.GREEN + "✓ Custom Model Data: Supported");
        } else {
            sender.sendMessage(ChatColor.RED + "✗ Custom Model Data: Not available");
        }
        
        // Advanced features
        if (VersionCompatibility.supportsAdvancedFeatures()) {
            sender.sendMessage(ChatColor.GREEN + "✓ Advanced Features: Available (" + VersionCompatibility.getPlatform() + ")");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "⚠ Advanced Features: Limited (consider upgrading to Paper)");
        }
        
        sender.sendMessage(ChatColor.GOLD + "\n=== Overall Status ===");
        if (VersionCompatibility.isSupported()) {
            sender.sendMessage(ChatColor.GREEN + "✓ This server version is supported!");
        } else {
            sender.sendMessage(ChatColor.RED + "⚠ This server version has limited support");
        }
        
        sender.sendMessage(ChatColor.GRAY + VersionCompatibility.getCompatibilityInfo());
        sender.sendMessage(ChatColor.GOLD + "================================");
    }


    private void handleAuthor(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Extra Mace Limiter ===");
        sender.sendMessage(ChatColor.YELLOW + "Author: " + ChatColor.WHITE + plugin.getDescription().getAuthors().get(0));
        sender.sendMessage(ChatColor.YELLOW + "Website: " + ChatColor.WHITE + plugin.getDescription().getWebsite());
    }

    private void handleIdentify(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return;
        }
        
        Player player = (Player) sender;
        org.bukkit.inventory.ItemStack item = player.getInventory().getItemInMainHand();
        
        if (item == null || item.getType() == org.bukkit.Material.AIR) {
            sender.sendMessage(ChatColor.RED + "You must hold an item in your main hand!");
            return;
        }
        
        sender.sendMessage(ChatColor.GOLD + "=== Item Information ===");
        sender.sendMessage(ChatColor.YELLOW + "Material: " + ChatColor.WHITE + item.getType().name());
        sender.sendMessage(ChatColor.YELLOW + "Amount: " + ChatColor.WHITE + item.getAmount());
        
        if (item.hasItemMeta()) {
            org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
            
            if (meta.hasDisplayName()) {
                sender.sendMessage(ChatColor.YELLOW + "Display Name: " + ChatColor.WHITE + meta.getDisplayName());
            }
            
            if (meta.hasCustomModelData()) {
                int customModelData = meta.getCustomModelData();
                sender.sendMessage(ChatColor.YELLOW + "Custom Model Data: " + ChatColor.WHITE + customModelData);
                sender.sendMessage(ChatColor.GRAY + "Config format: " + ChatColor.WHITE + 
                    "additional-blocked-items.custom-model-data." + item.getType().name() + ": [" + customModelData + "]");
            } else {
                sender.sendMessage(ChatColor.GRAY + "No custom model data");
            }
            
            if (meta.hasLore()) {
                sender.sendMessage(ChatColor.YELLOW + "Has Lore: " + ChatColor.WHITE + "Yes (" + meta.getLore().size() + " lines)");
            }
        } else {
            sender.sendMessage(ChatColor.GRAY + "No item metadata");
        }
        
        sender.sendMessage(ChatColor.GOLD + "======================");
    }


    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Extra Mace Limiter Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/extramacelimiter reload" + ChatColor.GRAY + " - Reload the plugin configuration");
        sender.sendMessage(ChatColor.YELLOW + "/extramacelimiter version" + ChatColor.GRAY + " - Show plugin version information");
        sender.sendMessage(ChatColor.YELLOW + "/extramacelimiter compatibility" + ChatColor.GRAY + " - Show detailed server compatibility info");
        sender.sendMessage(ChatColor.YELLOW + "/extramacelimiter author" + ChatColor.GRAY + " - Show plugin author and website info");
        sender.sendMessage(ChatColor.YELLOW + "/extramacelimiter identify" + ChatColor.GRAY + " - Identify held item's material and custom model data");
        sender.sendMessage(ChatColor.YELLOW + "/extramacelimiter help" + ChatColor.GRAY + " - Show this help menu");
        sender.sendMessage(ChatColor.GOLD + "Aliases: " + ChatColor.WHITE + "/eml, /macelimiter");
        sender.sendMessage(ChatColor.GOLD + "=====================================");
    }


    private int getBlockedStorageCount() {
        int count = 0;
        String[] storageTypes = {"chest", "ender-chest", "barrel", "shulker-box", "hopper", "dropper", "dispenser", "furnace", "blast-furnace", "smoker", "crafter"};
        
        for (String storage : storageTypes) {
            if (plugin.getConfig().getBoolean("blocked-storages." + storage, true)) {
                count++;
            }
        }
        
        return count;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        // dynamic tab completion if they have perms
        if (!sender.hasPermission("extramacelimiter.admin")) {
            return completions;
        }

        if (args.length == 1) {
            // dynamic compeltions
            String[] subCommands = {"reload", "version", "compatibility", "compat", "author", "identify", "help"};
            String input = args[0].toLowerCase();
            
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(input)) {
                    completions.add(subCommand);
                }
            }
        }

        return completions;
    }
}