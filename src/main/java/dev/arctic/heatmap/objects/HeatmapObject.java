package dev.arctic.heatmap.objects;

import dev.arctic.heatmap.Heatmap;
import dev.arctic.heatmap.HeatmapManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import static dev.arctic.heatmap.Heatmap.plugin;

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
        // Check if preProcessedData is null
        if (this.preProcessedData == null) {
            plugin.getLogger().log(Level.WARNING, "Heatmap contains no preprocessed data");
            return; // No data to process
        }

        // Initialize generatedHeatmap
        this.generatedHeatmap = new HashMap<>();

        // Directly process each Trail in preProcessedData
        for (Trail trail : preProcessedData.values()) {
            for (Location location : trail.getLocations()) {
                // Clean the location based on the scalar value
                Location cleanedLocation = cleanLocation(location, scalar);

                // Increment the count for this cleaned location
                this.generatedHeatmap.merge(cleanedLocation, 1, Integer::sum);
            }
        }
    }

    private Location cleanLocation(Location original, int scalar) {
        // Adjust the location coordinates based on the scalar value to "clean" them into nodes
        int x = (original.getBlockX() / scalar) * scalar;
        int y = (original.getBlockY() / scalar) * scalar;
        int z = (original.getBlockZ() / scalar) * scalar;

        return new Location(original.getWorld(), x, y, z);
    }
}
