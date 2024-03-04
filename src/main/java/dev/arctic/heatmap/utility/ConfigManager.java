package dev.arctic.heatmap.utility;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static dev.arctic.heatmap.Heatmap.plugin;

public class ConfigManager {

    private static final String CONFIG_PATH = "config.yml";
    private static File configFile;
    private static FileConfiguration config;

    static {
        configFile = new File(plugin.getDataFolder().getAbsolutePath(), CONFIG_PATH);
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static void ensureConfig() {
        try {
            if (!configFile.exists()) {
                saveDefaultConfig();
            } else {
                updateConfig();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveDefaultConfig() throws IOException {
        String defaultConfigContent = getDefaultConfigContent();
        plugin.getDataFolder().mkdirs(); // Ensure the plugin directory exists
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            writer.write(defaultConfigContent);
        }
    }

    private static void updateConfig() throws IOException {
        Map<String, Object> existingValues = new HashMap<>();
        // Load existing config values or use defaults
        existingValues.put("storage.type", config.getString("storage.type", "H2"));
        existingValues.put("storage.h2.username", config.getString("storage.h2.username", "heatmap_admin"));
        existingValues.put("storage.h2.password", config.getString("storage.h2.password", "heatmap_admin"));
        existingValues.put("storage.sql.url", config.getString("storage.sql.url", "jdbc:mysql://localhost:3306/yourdatabase"));
        existingValues.put("storage.sql.username", config.getString("storage.sql.username", "yourusername"));
        existingValues.put("storage.sql.password", config.getString("storage.sql.password", "yourpassword"));
        existingValues.put("storage.sql.driver", config.getString("storage.sql.driver", "com.mysql.cj.jdbc.Driver"));
        existingValues.put("scalar", config.getInt("scalar", 1));

        // Save updated config
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            writer.write(getUpdatedConfigContent(existingValues));
        }
    }

    private static String getDefaultConfigContent() {
        return """
                # ▬▬▬▬| Config |▬▬▬▬
                # this is a pretty bare-bones plugin at the moment
                # and is still actively in development to add new
                # features!
                
                # ---| Storage Strategy |---
                # How should we store data?
                storage:
                  type: "H2" # Options: "json", "H2", "SQL"
                  h2:
                    username: "heatmap_admin"
                    password: "heatmap_admin"
                  sql:
                    url: "jdbc:mysql://localhost:3306/yourdatabase"
                    username: "yourusername"
                    password: "yourpassword"
                    driver: "com.mysql.cj.jdbc.Driver" #this shouldn't change.
                
                # ▬▬▬▬| Heatmap Node Configuration |▬▬▬▬
                #
                # ---| Scalar |---
                # How far apart should we process movement data.
                # this is more effective for maps of large scale
                # however it does generate "lost" data which can be
                # compared to individual player trails.
                scalar: 1
                """;
    }

    private static String getUpdatedConfigContent(Map<String, Object> values) {
        return String.format("""
                # ▬▬▬▬| Config |▬▬▬▬
                # this is a pretty bare-bones plugin at the moment
                # and is still actively in development to add new
                # features!
                
                # ---| Storage Strategy |---
                # How should we store data?
                storage:
                  type: "%s"
                  h2:
                    username: "%s"
                    password: "%s"
                  sql:
                    url: "%s"
                    username: "%s"
                    password: "%s"
                    driver: "%s"
                
                # ▬▬▬▬| Heatmap Node Configuration |▬▬▬▬
                #
                # ---| Scalar |---
                # How far apart should we process movement data.
                # this is more effective for maps of large scale
                # however it does generate "lost" data which can be
                # compared to individual player trails.
                scalar: %d
                """,
                values.get("storage.type"),
                values.get("storage.h2.username"),
                values.get("storage.h2.password"),
                values.get("storage.sql.url"),
                values.get("storage.sql.username"),
                values.get("storage.sql.password"),
                values.get("storage.sql.driver"),
                values.get("scalar"));
    }
}
