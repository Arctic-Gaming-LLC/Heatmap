package dev.arctic.heatmap.utility;

import dev.arctic.heatmap.objects.HeatmapObject;

import java.util.HashMap;

import static dev.arctic.heatmap.Heatmap.plugin;

public class DataManagement {
    private static StorageStrategy storageStrategy;

    public DataManagement() {
        // Initialize based on config
        String storageType = plugin.getConfig().getString("storage.type");
        switch (storageType.toLowerCase()) {
            case "h2":
                storageStrategy = new H2StorageStrategy();
                break;
            case "sql":
                storageStrategy = new SQLStorageStrategy(
                        plugin.getConfig().getString("storageStrategy.sql.url"),
                        plugin.getConfig().getString("storageStrategy.sql.username"),
                        plugin.getConfig().getString("storageStrategy.sql.password")
                );
                break;
            default:
                storageStrategy = new JsonStorageStrategy();
                break;
        }
    }
    public static void saveHeatmapsSync(HashMap<String, HeatmapObject> heatmaps){
        storageStrategy.saveHeatmapsSync(heatmaps);
    }

    public static void saveHeatmaps(HashMap<String, HeatmapObject> heatmaps) {
        storageStrategy.saveHeatmaps(heatmaps);
    }

    public static HashMap<String, HeatmapObject> loadHeatmaps() {
        return storageStrategy.loadHeatmaps();
    }

    public static void removeHeatmap(String regionID){
        storageStrategy.removeHeatmap(regionID);
    }
}

