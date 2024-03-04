package dev.arctic.heatmap.utility;

import dev.arctic.heatmap.objects.HeatmapObject;
import static dev.arctic.heatmap.Heatmap.dataManagement;
import static dev.arctic.heatmap.Heatmap.plugin;

import java.util.HashMap;
import java.util.logging.Level;

public class HeatmapManager {

    public static HashMap<String, HeatmapObject> heatmaps = new HashMap<>();

    public static void addHeatmap(String regionID, HeatmapObject heatmap) {
        heatmaps.put(regionID, heatmap);
        dataManagement.saveHeatmaps(heatmaps);
    }

    public static HeatmapObject getHeatmap(String regionID) {
        return heatmaps.get(regionID);
    }

    public static void removeHeatmap(String regionID) {
        heatmaps.remove(regionID);
        dataManagement.saveHeatmaps(heatmaps);
    }

    public static void updateHeatmap(String regionID, HeatmapObject updatedHeatmap) {
        heatmaps.put(regionID, updatedHeatmap);
        dataManagement.saveHeatmaps(heatmaps);
    }

    public static void saveHeatmaps() {
        dataManagement.saveHeatmaps(heatmaps);
    }

    public static void loadHeatmaps() {
        HashMap<String, HeatmapObject> loadedMap = dataManagement.loadHeatmaps();
        for (HeatmapObject heatmap: loadedMap.values()){
            heatmaps.put(heatmap.getRegionID(), heatmap);
        }
        if (heatmaps == null || heatmaps.isEmpty()) {
            plugin.getLogger().log(Level.WARNING, "No heatmaps loaded or failed to load heatmaps.");
        } else {
            plugin.getLogger().log(Level.WARNING, heatmaps.size() + " heatmaps loaded successfully.");
        }
    }
}
