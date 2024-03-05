package dev.arctic.heatmap.utility;

import com.google.gson.Gson;
import dev.arctic.heatmap.objects.HeatmapObject;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static dev.arctic.heatmap.Heatmap.plugin;

public class SQLStorageStrategy implements StorageStrategy {
    private final String url;
    private final String username;
    private final String password;
    private final Gson gson = new Gson();

    public SQLStorageStrategy(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    private void ensureTableExists() {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS heatmap_storage (" +
                    "regionID VARCHAR(255) PRIMARY KEY," +
                    "data TEXT NOT NULL" +
                    ");";
            try (PreparedStatement statement = connection.prepareStatement(createTableSQL)) {
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void saveHeatmapsSync(HashMap<String, HeatmapObject> heatmaps){
        ensureTableExists();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "REPLACE INTO heatmap_storage (regionID, data) VALUES (?, ?);";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                for (Map.Entry<String, HeatmapObject> entry : heatmaps.entrySet()) {
                    statement.setString(1, entry.getKey());
                    statement.setString(2, gson.toJson(entry.getValue()));
                    statement.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveHeatmaps(HashMap<String, HeatmapObject> heatmaps) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            ensureTableExists();
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                String query = "REPLACE INTO heatmap_storage (regionID, data) VALUES (?, ?);";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    for (Map.Entry<String, HeatmapObject> entry : heatmaps.entrySet()) {
                        statement.setString(1, entry.getKey());
                        statement.setString(2, gson.toJson(entry.getValue()));
                        statement.executeUpdate();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public HashMap<String, HeatmapObject> loadHeatmaps() {
        ensureTableExists();
        HashMap<String, HeatmapObject> loadedHeatmaps = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT regionID, data FROM heatmap_storage;";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                ResultSet results = statement.executeQuery();
                while (results.next()) {
                    String regionID = results.getString("regionID");
                    HeatmapObject heatmap = gson.fromJson(results.getString("data"), HeatmapObject.class);
                    loadedHeatmaps.put(regionID, heatmap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loadedHeatmaps;
    }


    @Override
    public void removeHeatmap(String regionID) {
        ensureTableExists();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "DELETE FROM heatmap_storage WHERE regionID = ?;";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, regionID);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}