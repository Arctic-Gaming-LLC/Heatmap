package dev.arctic.heatmap.utility;

import dev.arctic.heatmap.objects.HeatmapObject;

import java.util.HashMap;

public interface StorageStrategy {
    void saveHeatmapsSync(HashMap<String, HeatmapObject> heatmaps);
    void saveHeatmaps(HashMap<String, HeatmapObject> heatmaps);
    HashMap<String, HeatmapObject> loadHeatmaps();
}
