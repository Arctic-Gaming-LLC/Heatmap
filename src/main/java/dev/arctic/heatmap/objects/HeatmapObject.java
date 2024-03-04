package dev.arctic.heatmap.objects;

import dev.arctic.heatmap.Heatmap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class HeatmapObject {
    private final String regionID;
    private HashMap<UUID, Trail> preProcessedData;
    private Map<Location, Integer> generatedHeatmap;
    private int scalar;
    private boolean tracking;

    public HeatmapObject(String regionID) {
        this.regionID = regionID;
        this.scalar = Heatmap.scalar;
        this.preProcessedData = new HashMap<>();
        this.generatedHeatmap = new HashMap<>();
        this.tracking = true;
    }

    public void addPreProcessedData(UUID playerUUID, Trail trail) {
        this.preProcessedData.put(playerUUID, trail);
    }

    public void processData() {
        // Run task asynchronously
        new BukkitRunnable() {
            @Override
            public void run() {
                Map<Location, Integer> localGeneratedHeatmap = new HashMap<>();
                Map<String, Integer> tempMap = new HashMap<>();
                org.bukkit.World world = null;

                for (Trail trail : preProcessedData.values()) {
                    for (Location location : trail.getLocations()) {
                        if (world == null) {
                            world = location.getWorld();
                        }
                        Location cleanedLocation = cleanLocation(location, scalar);
                        String key = generateKeyForLocation(cleanedLocation);
                        adjustAndAggregateY(tempMap, key, cleanedLocation);
                    }
                }

                if (world == null) {
                    return; // Handle the case where no locations have been processed
                }

                for (Map.Entry<String, Integer> entry : tempMap.entrySet()) {
                    String[] parts = entry.getKey().split(":");
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    int z = Integer.parseInt(parts[2]);
                    Location newLocation = new Location(world, x, y, z);
                    localGeneratedHeatmap.put(newLocation, entry.getValue());
                }

                // Update the generatedHeatmap on the main thread
                Bukkit.getScheduler().runTask(Heatmap.plugin, () -> generatedHeatmap = localGeneratedHeatmap);
            }
        }.runTaskAsynchronously(Heatmap.plugin);
    }


    private Location cleanLocation(Location original, int scalar) {
        int x = (original.getBlockX() / scalar) * scalar;
        int y = (original.getBlockY() / scalar) * scalar;
        int z = (original.getBlockZ() / scalar) * scalar;
        return new Location(original.getWorld(), x, y, z);
    }

    private String generateKeyForLocation(Location location) {
        return location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();
    }

    private void adjustAndAggregateY(Map<String, Integer> tempMap, String key, Location location) {
        boolean foundCloseY = false;
        for (String existingKey : new HashSet<>(tempMap.keySet())) {
            String[] parts = existingKey.split(":");
            int existingX = Integer.parseInt(parts[0]);
            int existingZ = Integer.parseInt(parts[2]);
            if (existingX == location.getBlockX() && existingZ == location.getBlockZ()) {
                double existingY = Double.parseDouble(parts[1]);
                if (Math.abs(existingY - location.getBlockY()) <= 1.2) {
                    tempMap.merge(existingKey, 1, Integer::sum);
                    foundCloseY = true;
                    break;
                }
            }
        }

        if (!foundCloseY) {
            tempMap.merge(key, 1, Integer::sum);
        }
    }
}

