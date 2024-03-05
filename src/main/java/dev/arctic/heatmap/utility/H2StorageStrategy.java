package dev.arctic.heatmap.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.arctic.heatmap.objects.HeatmapObject;
import dev.arctic.heatmap.objects.LocationAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static dev.arctic.heatmap.Heatmap.plugin;

public class H2StorageStrategy implements StorageStrategy {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Location.class, new LocationAdapter())
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .create();

    @Override
    public void saveHeatmapsSync(HashMap<String, HeatmapObject> heatmaps) {
        ensureTableExists();
        String url = getConnectionUrl();
        String sql = "MERGE INTO heatmaps KEY(regionID) VALUES (?, ?)";

        try {
            // Attempt to establish connection
            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                for (Map.Entry<String, HeatmapObject> entry : heatmaps.entrySet()) {
                    stmt.setString(1, entry.getKey());
                    String heatmapData = gson.toJson(entry.getValue());
                    stmt.setString(2, heatmapData);
                    stmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveHeatmaps(HashMap<String, HeatmapObject> heatmaps) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> saveHeatmapsSync(heatmaps));
    }

    @Override
    public HashMap<String, HeatmapObject> loadHeatmaps() {
        ensureTableExists();
        HashMap<String, HeatmapObject> loadedHeatmaps = new HashMap<>();
        String url = getConnectionUrl();
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM heatmaps")) {

            while (rs.next()) {
                String regionID = rs.getString("regionID");
                String heatmapData = rs.getString("data");
                HeatmapObject heatmapObject = gson.fromJson(heatmapData, HeatmapObject.class);
                loadedHeatmaps.put(regionID, heatmapObject);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loadedHeatmaps;
    }


    @Override
    public void removeHeatmap(String regionID) {
        String url = getConnectionUrl();
        String sql = "DELETE FROM heatmaps WHERE regionID = ?";

        try {
            // Attempt to establish connection and prepare the statement
            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                // Set the regionID parameter and execute the update
                stmt.setString(1, regionID);
                int affectedRows = stmt.executeUpdate();

                // Optionally, log the removal
                if (affectedRows > 0) {
                    System.out.println("Successfully removed heatmap with regionID: " + regionID);
                } else {
                    System.out.println("No heatmap found with regionID: " + regionID);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ensureTableExists() {
        String url = getConnectionUrl();
        String createTableSQL = "CREATE TABLE IF NOT EXISTS heatmaps (" +
                "regionID VARCHAR(255) PRIMARY KEY," +
                "data TEXT NOT NULL" +
                ");";
        try {
            Class.forName("org.h2.Driver"); // Explicitly load the H2 driver
            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
                stmt.execute();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private String getConnectionUrl() {
        String dbFileName = "heatmapdb";
        String path = plugin.getDataFolder().getAbsolutePath() + "/" + dbFileName;
        return "jdbc:h2:file:" + path + ";DB_CLOSE_DELAY=-1";
    }
}
