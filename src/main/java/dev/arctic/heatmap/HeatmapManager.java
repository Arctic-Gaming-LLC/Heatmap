package dev.arctic.heatmap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.arctic.heatmap.objects.HeatmapObject;
import dev.arctic.heatmap.objects.LocationAdapter;
import org.bukkit.Location;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;

import static dev.arctic.heatmap.Heatmap.plugin;

public class HeatmapManager {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Location.class, new LocationAdapter())
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .create();
    public static HashMap<String, HeatmapObject> heatmaps = new HashMap<>();

    public static void addHeatmap(String regionID, HeatmapObject heatmap) {
        heatmaps.put(regionID, heatmap);
        saveHeatmaps();
    }

    public static HeatmapObject getHeatmap(String regionID) {
        return heatmaps.get(regionID);
    }

    public static void removeHeatmap(String regionID) {
        heatmaps.remove(regionID);
        saveHeatmaps();
    }

    public static void updateHeatmap(String regionID, HeatmapObject updatedHeatmap) {
        heatmaps.put(regionID, updatedHeatmap);
        saveHeatmaps();
    }

    public static void saveHeatmaps() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            boolean dirsMade = dataFolder.mkdirs();
            if (!dirsMade) {
                plugin.getLogger().warning("Could not create plugin directory.");
                return;
            }
        }
        File heatmapFile = new File(dataFolder, "heatmaps.json");

        try (Writer writer = new FileWriter(heatmapFile)) {
            gson.toJson(heatmaps, writer);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save heatmaps: " + e.getMessage());
        }
    }

    public static void loadHeatmaps() {
        File heatmapFile = new File(plugin.getDataFolder(), "heatmaps.json");
        if (!heatmapFile.exists()) {
            return;
        }

        try (Reader reader = new FileReader(heatmapFile)) {
            Type type = new TypeToken<HashMap<String, HeatmapObject>>() {}.getType();
            heatmaps = gson.fromJson(reader, type);
        } catch (FileNotFoundException e) {
            plugin.getLogger().warning("Heatmaps file not found: " + e.getMessage());
        } catch (IOException e) {
            plugin.getLogger().warning("Could not read heatmaps: " + e.getMessage());
        }

        if (heatmaps == null) {
            heatmaps = new HashMap<>();
        }
    }
}
