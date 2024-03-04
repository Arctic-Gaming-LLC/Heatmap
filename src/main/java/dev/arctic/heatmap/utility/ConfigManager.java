package dev.arctic.heatmap.utility;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

import static dev.arctic.heatmap.Heatmap.plugin;

public class ConfigManager {

    public static void ensureConfig() {
        try {
            File configFile = new File(plugin.getDataFolder(), "config.yml");
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
        String defaultConfigContent = """
                # ▬▬▬▬| Config |▬▬▬▬
                # this is a pretty bare-bones plugin at the moment
                # and is still actively in development to add new
                # features!
                configVersion: 2.0
                            
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

        File configFile = new File(plugin.getDataFolder(), "config.yml");
        plugin.getDataFolder().mkdirs(); // Ensure the plugin directory exists
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            writer.write(defaultConfigContent);
        }
    }

    private static void updateConfig() throws IOException {
        File configFile = new File(plugin.getDataFolder(), "config.yml");

        String updatedConfigContent = """
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

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            writer.write(updatedConfigContent);
        }
    }
}