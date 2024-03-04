package dev.arctic.heatmap.utility;

import dev.arctic.heatmap.objects.HeatmapObject;
import static dev.arctic.heatmap.Heatmap.dataManagement;

import java.util.HashMap;

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
        heatmaps = dataManagement.loadHeatmaps();
    }
}
