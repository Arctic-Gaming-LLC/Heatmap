package dev.arctic.heatmap.commands;

import dev.arctic.heatmap.visualization.HeatmapVisualizer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

import static dev.arctic.heatmap.Heatmap.plugin;

public class ViewHeatmapCommand {
    private static final HashMap<UUID, Boolean> viewingState = new HashMap<>();
    private static final HashMap<UUID, BukkitTask> tasks = new HashMap<>();

    public void execute(Player player, String regionID) {
        execute(player, regionID, null); // Call the overloaded method with null UUID
    }

    public void execute(Player player, String regionID, UUID targetUUID) {
        UUID playerId = player.getUniqueId();
        boolean isViewing = viewingState.getOrDefault(playerId, false);

        if (!isViewing) {
            player.sendMessage("Heatmap viewing " + (targetUUID == null ? "enabled" : "for player " + targetUUID) + " for region: " + regionID);
            viewingState.put(playerId, true);

            // Schedule a repeating task to display the heatmap
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if (targetUUID == null) {
                    HeatmapVisualizer.displayHeatmap(regionID);
                } else {
                    HeatmapVisualizer.displayPlayerTrail(regionID, targetUUID);
                }
            }, 0L, 10L); // 20 ticks = 1 second interval

            tasks.put(playerId, task);
        } else {
            player.sendMessage("Heatmap viewing " + (targetUUID == null ? "disabled" : "for player " + targetUUID) + " for region: " + regionID);
            viewingState.put(playerId, false);

            // Cancel the repeating task
            if (tasks.containsKey(playerId)) {
                tasks.get(playerId).cancel();
                tasks.remove(playerId);
            }
        }
    }
}