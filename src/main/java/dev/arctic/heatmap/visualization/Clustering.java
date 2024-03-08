package dev.arctic.heatmap.visualization;

import dev.arctic.heatmap.Heatmap;
import dev.arctic.heatmap.objects.HeatmapObject;
import dev.arctic.heatmap.objects.SquareZone;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import smile.clustering.DBSCAN;
import smile.neighbor.KDTree;
import xyz.jpenilla.squaremap.api.Point;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Clustering {
    private static final double epsilon = 10.0;
    private static final int minPts = 5;
    public static final CopyOnWriteArrayList<SquareZone> processedZones = new CopyOnWriteArrayList<>();

    public static void performDBSCANAsync(HeatmapObject heatmapObject) {
        new BukkitRunnable() {
            @Override
            public void run() {
                processedZones.clear();
                analyzeAndCluster(heatmapObject);
            }
        }.runTaskAsynchronously(Heatmap.plugin);
    }

    private static void analyzeAndCluster(HeatmapObject heatmapObject) {
        int maxHits = heatmapObject.getGeneratedHeatmap().values().stream().max(Integer::compare).orElse(0);
        int minHits = heatmapObject.getGeneratedHeatmap().values().stream().min(Integer::compare).orElse(0);

        // Define thresholds for hit ratio groups
        int range = maxHits - minHits;
        int thresholdOne = minHits + (range / 3);
        int thresholdTwo = minHits + 2 * (range / 3);

        Map<Location, Integer> groupLow = new HashMap<>();
        Map<Location, Integer> groupMedium = new HashMap<>();
        Map<Location, Integer> groupHigh = new HashMap<>();

        // Group locations based on hit ratios
        heatmapObject.getGeneratedHeatmap().forEach((location, hits) -> {
            if (hits <= thresholdOne) {
                groupLow.put(location, hits);
            } else if (hits <= thresholdTwo) {
                groupMedium.put(location, hits);
            } else {
                groupHigh.put(location, hits);
            }
        });

        // Process each group with clustering
        processGroupWithClustering(groupLow, Color.GREEN);
        processGroupWithClustering(groupMedium, Color.YELLOW);
        processGroupWithClustering(groupHigh, Color.RED);
    }

    private static void processGroupWithClustering(Map<Location, Integer> group, Color color) {
        if (!group.isEmpty()) {
            List<double[]> points = new ArrayList<>();
            group.keySet().forEach(location -> points.add(new double[]{location.getX(), location.getZ()}));

            double[][] locationsArray = points.toArray(new double[points.size()][]);
            KDTree<double[]> kdTree = new KDTree<>(locationsArray, locationsArray);
            int[] labels = new int[locationsArray.length];

            new DBSCAN<>(minPts, epsilon, kdTree, locationsArray.length, labels);

            createSquareZonesFromPoints(points, color);
        }
    }

    private static void createSquareZonesFromPoints(List<double[]> points, java.awt.Color color) {
        double minX = points.stream().mapToDouble(p -> p[0]).min().orElse(0);
        double maxX = points.stream().mapToDouble(p -> p[0]).max().orElse(0);
        double minZ = points.stream().mapToDouble(p -> p[1]).min().orElse(0);
        double maxZ = points.stream().mapToDouble(p -> p[1]).max().orElse(0);

        SquareZone newZone = new SquareZone(minX, minZ, maxX, maxZ, color);
        Clustering.processedZones.add(newZone);
    }
}
