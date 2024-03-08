package dev.arctic.heatmap.commands;

import dev.arctic.heatmap.Heatmap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static dev.arctic.heatmap.Heatmap.mapPluginStatus;

public class CommandManager implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("Usage: /heatmap <create|close|view|remove> <regionID>");
            return true;
        }

        String action = args[0];
        String regionID = args[1];

        switch (action.toLowerCase()) {
            case "create":
                // Implement create heatmap logic
                if (player.hasPermission("heatmap.create") || player.hasPermission("heatmap.admin") || player.isOp()) {
                    player.sendMessage("Creating heatmap for region: " + regionID);
                    if (new CreateHeatMapCommand().execute(regionID)) {
                        player.sendMessage("Heatmap Initialized");
                    } else {
                        player.sendMessage("Heatmap NOT initialized, something went wrong...");
                    }
                }
                break;
            case "close":
                // Implement close heatmap logic
                if (player.hasPermission("heatmap.close") || player.hasPermission("heatmap.admin") || player.isOp()) {
                    player.sendMessage("Closing heatmap for region: " + regionID);
                    if (new ProcessHeatMapCommand().execute(regionID)) {
                        player.sendMessage("Heatmap Processed! Available for view!");
                    } else {
                        player.sendMessage("Heatmap was unable to be processed - check console for more information");
                    }
                }
                break;
            case "view":
                if (player.hasPermission("heatmap.view") || player.hasPermission("heatmap.admin") || player.isOp()) {
                    if (args.length == 3) { // Expecting a UUID as a third argument
                        UUID uuid;
                        try {
                            uuid = UUID.fromString(args[2]);
                        } catch (IllegalArgumentException e) {
                            player.sendMessage("Invalid UUID format.");
                            return true;
                        }
                        new ViewHeatmapCommand().execute(player, regionID, uuid);
                        player.sendMessage("Viewing individual player trail.");
                    } else {
                        new ViewHeatmapCommand().execute(player, regionID);
                        player.sendMessage("Viewing heatmap for region: " + regionID);
                    }
                }
                break;
            case "remove":
                // Implement remove heatmap logic
                if(player.hasPermission("heatmap.remove") || player.hasPermission("heatmap.admin") || player.isOp()) {
                    player.sendMessage("Removing heatmap for region: " + regionID);
                    if (new RemoveHeatmapCommand().execute(regionID)) {
                        return true;
                    } else {
                        player.sendMessage("Heatmap unable to be removed.");
                    }
                }
                break;
            case "render":
                if (player.hasPermission("heatmap.render") || player.hasPermission("heatmap.admin") || player.isOp()) {
                    if (mapPluginStatus == 0) {
                        player.sendMessage("No map plugin found. Please install a supported map plugin.");
                        return true;
                    }
                    new RenderHeatmapCommand().execute(regionID);
                    player.sendMessage("Heatmap rendering initiated for " + regionID + ".");
                } else {
                    player.sendMessage("You do not have permission to render heatmaps.");
                }
                break;
            default:
                player.sendMessage("Invalid command. Use /heatmap <create|close|view|remove> <regionID>");
                break;
        }
        return true;
    }
}
