package dev.arctic.heatmap.visualization;

import dev.arctic.heatmap.utility.HeatmapManager;
import dev.arctic.heatmap.objects.HeatmapObject;
import dev.arctic.heatmap.objects.Trail;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;
import static dev.arctic.heatmap.Heatmap.plugin;
import static dev.arctic.heatmap.Heatmap.scalar;

public class HeatmapVisualizer {

    public static void displayHeatmap(String regionID) {
        HeatmapObject heatmap = HeatmapManager.heatmaps.get(regionID);
        if (heatmap == null) {
            plugin.getLogger().log(Level.WARNING, "No tracked heatmaps!");
            return;
        }

        Map<Location, Integer> data = heatmap.getGeneratedHeatmap();
        if (data == null || data.isEmpty()) {
            plugin.getLogger().log(Level.WARNING, "Heatmap contains no data?");
            return;
        }

        int minValue = 1; // Assuming 1 as the lowest since NONE means no particles
        int maxValue = data.values().stream().max(Integer::compare).orElse(minValue);

        List<Location> nodes = new ArrayList<>(data.keySet());
        for (Location node : nodes) {
            List<Location> nearestNodes = findNearestNodesWithinScalar(node, nodes, scalar);
            for (Location target : nearestNodes) {
                drawParticleLine(node, target, data.get(node), data.get(target), minValue, maxValue);
            }
        }
    }

    public static void displayPlayerTrail(String regionID, UUID playerUUID) {
        HeatmapObject heatmap = HeatmapManager.heatmaps.get(regionID);
        if (heatmap == null) {
            plugin.getLogger().log(Level.WARNING, "No tracked heatmaps for region: " + regionID);
            return;
        }

        Trail trail = heatmap.getPreProcessedData().get(playerUUID);
        if (trail == null) {
            plugin.getLogger().log(Level.WARNING, "No trail data for player UUID: " + playerUUID);
            return;
        }

        List<Location> playerTrailLocations = trail.getLocations();
        if (playerTrailLocations.isEmpty()) {
            plugin.getLogger().log(Level.WARNING, "Player trail is empty for UUID: " + playerUUID);
            return;
        }

        // Dynamically calculate minValue and maxValue from the heatmap data for consistent coloring
        Map<Location, Integer> heatmapData = heatmap.getGeneratedHeatmap();
        int minValue = heatmapData.values().stream().min(Integer::compare).orElse(1);
        int maxValue = heatmapData.values().stream().max(Integer::compare).orElse(1);

        playerTrailLocations.forEach(location -> {
            Particle.DustOptions particleOptions = determineParticleColor(1, minValue, maxValue);
            Location particleLocation = location.clone().add(0, 1, 0);
            World world = location.getWorld();
            if (world != null) {
                world.spawnParticle(Particle.REDSTONE, particleLocation, 1, particleOptions);
            }
        });
    }

    private static List<Location> findNearestNodesWithinScalar(Location node, List<Location> nodes, int scalar) {
        double adjustedScalar = scalar + scalar * 0.1; // Adjusting scalar to ensure coverage
        return nodes.stream()
                .filter(other -> !other.equals(node) && node.distance(other) <= adjustedScalar)
                .collect(Collectors.toList());
    }

    private static void drawParticleLine(Location start, Location end, int startCount, int endCount, int minValue, int maxValue) {
        World world = start.getWorld();
        if (world == null || !start.getWorld().equals(end.getWorld())) return;

        Vector startVector = start.toVector();
        Vector endVector = end.toVector();
        Vector difference = endVector.clone().subtract(startVector);
        int steps = (int) (start.distance(end) * 10); // Adjust steps for smoother lines

        for (int i = 0; i <= steps; i++) {
            Vector step = difference.clone().multiply((double) i / steps);
            Location particleLocation = start.clone().add(step).add(0, 1, 0);
            Particle.DustOptions particleOptions = getBlendedParticleColor(startCount, endCount, i, steps, minValue, maxValue);
            world.spawnParticle(Particle.REDSTONE, particleLocation, 1, particleOptions);
        }
    }

    private static Particle.DustOptions getBlendedParticleColor(int startCount, int endCount, int step, int totalSteps, int minValue, int maxValue) {
        float startRatio = (startCount - minValue) / (float) (maxValue - minValue);
        float endRatio = (endCount - minValue) / (float) (maxValue - minValue);

        Color startColor = getColorFromRatio(startRatio);
        Color endColor = getColorFromRatio(endRatio);

        double ratio = (double) step / totalSteps;
        int red = (int) (startColor.getRed() * (1 - ratio) + endColor.getRed() * ratio);
        int green = (int) (startColor.getGreen() * (1 - ratio) + endColor.getGreen() * ratio);
        int blue = (int) (startColor.getBlue() * (1 - ratio) + endColor.getBlue() * ratio);

        return new Particle.DustOptions(Color.fromRGB(red, green, blue), 1);
    }

    private static Particle.DustOptions determineParticleColor(int count, int minValue, int maxValue) {
        float ratio = (count - minValue) / (float) (maxValue - minValue);
        if (minValue == maxValue) ratio = 1.0f; // Avoid division by zero

        Color color = getColorFromRatio(ratio);
        return new Particle.DustOptions(color, 1);
    }

    private static Color getColorFromRatio(float ratio) {
        if (ratio < 0.15) return Color.GREEN;
        else if (ratio < 0.85) return Color.YELLOW;
        else if (ratio < 0.99) return Color.ORANGE;
        else return Color.RED;
    }
}
