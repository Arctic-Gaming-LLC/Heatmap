package dev.arctic.heatmap.utility;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.arctic.heatmap.objects.HeatmapObject;
import dev.arctic.heatmap.objects.LocationAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;

import static dev.arctic.heatmap.Heatmap.plugin;

public class JsonStorageStrategy implements StorageStrategy {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Location.class, new LocationAdapter())
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .create();

    @Override
    public void saveHeatmapsSync(HashMap<String, HeatmapObject> heatmaps) {
        File heatmapFile = new File(plugin.getDataFolder().getAbsolutePath() + "/heatmaps.json");
        try (Writer writer = new FileWriter(heatmapFile)) {
            gson.toJson(heatmaps, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveHeatmaps(HashMap<String, HeatmapObject> heatmaps) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            File heatmapFile = new File(plugin.getDataFolder().getAbsolutePath() + "/heatmaps.json");
            try (Writer writer = new FileWriter(heatmapFile)) {
                gson.toJson(heatmaps, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    public HashMap<String, HeatmapObject> loadHeatmaps() {
        File heatmapFile = new File(plugin.getDataFolder().getAbsolutePath() + "/heatmaps.json");
        if (!heatmapFile.exists()) {
            return new HashMap<>();
        }

        try (Reader reader = new FileReader(heatmapFile)) {
            Type type = new TypeToken<HashMap<String, HeatmapObject>>() {
            }.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}

