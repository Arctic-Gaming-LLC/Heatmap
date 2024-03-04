package dev.arctic.heatmap.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import dev.arctic.heatmap.utility.HeatmapManager;
import dev.arctic.heatmap.objects.HeatmapObject;
import dev.arctic.heatmap.objects.Trail;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class PlayerMoveEventListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        if (event.getFrom().distance(event.getTo()) < 0.2) {
            return; // Movement is not significant
        }

        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        Location loc = BukkitAdapter.adapt(event.getTo());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(event.getTo().getWorld()));
        ApplicableRegionSet set;
        if (regions != null) {
            set = regions.getApplicableRegions(loc.toVector().toBlockPoint());

            for (ProtectedRegion region : set.getRegions()) {

                if (HeatmapManager.heatmaps.containsKey(region.getId())) {

                    HeatmapObject heatmap = HeatmapManager.getHeatmap(region.getId());
                    if (heatmap.isTracking()) {
                        Trail trail;

                        if (heatmap.getPreProcessedData().containsKey(playerUUID)) {
                            trail = heatmap.getPreProcessedData().get(playerUUID);
                        } else {
                            trail = new Trail(playerUUID);
                        }
                        trail.addLocation(event.getTo());
                        heatmap.addPreProcessedData(playerUUID, trail);
                    }
                }
            }
        }
    }
}
