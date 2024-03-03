package dev.arctic.heatmap.utility;

import lombok.Data;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static dev.arctic.heatmap.Heatmap.plugin;

@Data
public class ConfigManager {


    public static HeatmapConfig createConfigObject() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        double configVersion = config.getDouble("configVersion");
        int scalar = config.getInt("scalar");

        return new HeatmapConfig(configVersion, scalar);
    }

    public static void updateConfig(HeatmapConfig newConfig) throws IOException {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        // Update values in the YamlConfiguration object
        config.set("configVersion", newConfig.getConfigVersion());
        config.set("scalar", newConfig.getScalar());

        // Save the YamlConfiguration back to the file
        config.save(configFile);
    }

    @Data
    public static class HeatmapConfig {
        private final double configVersion;
        private final int scalar;

        public HeatmapConfig(double configVersion, int scalar) {
            this.configVersion = configVersion;
            this.scalar = scalar;
        }
    }
}
