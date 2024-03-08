package dev.arctic.heatmap.visualization;

import dev.arctic.heatmap.objects.HeatmapObject;
import dev.arctic.heatmap.objects.SquareZone;
import org.bukkit.Bukkit;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import dev.arctic.heatmap.events.HeatmapRenderEvent;

import static dev.arctic.heatmap.Heatmap.plugin;

public class GridZone {
    private HeatmapObject heatmapObject;

    public GridZone(HeatmapObject heatmapObject) {
        this.heatmapObject = heatmapObject;
    }

    public void createGridZonesAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<SquareZone> gridZones = new ArrayList<>();
            int maxHits = heatmapObject.getGeneratedHeatmap().values().stream().max(Integer::compare).orElse(0);
            int minHits = heatmapObject.getGeneratedHeatmap().values().stream().min(Integer::compare).orElse(0);

            heatmapObject.getGeneratedHeatmap().forEach((location, hits) -> {
                Color color = determineColor(hits, minHits, maxHits);
                double scalar = heatmapObject.getScalar();
                double minX = location.getBlockX() - scalar / 2.0;
                double minZ = location.getBlockZ() - scalar / 2.0;
                double maxX = location.getBlockX() + scalar / 2.0;
                double maxZ = location.getBlockZ() + scalar / 2.0;

                SquareZone squareZone = new SquareZone(minX, minZ, maxX, maxZ, color);
                gridZones.add(squareZone);
            });

            Bukkit.getScheduler().runTask(plugin, () ->
                    Bukkit.getServer().getPluginManager().callEvent(new HeatmapRenderEvent(heatmapObject, gridZones))
            );
        });
    }


    private Color determineColor(int hits, int minHits, int maxHits) {
        float ratio = (float) (hits - minHits) / (maxHits - minHits);
        if (ratio < 0.3) return Color.GREEN;
        else if (ratio < 0.7) return Color.YELLOW;
        else return Color.RED;
    }
}
