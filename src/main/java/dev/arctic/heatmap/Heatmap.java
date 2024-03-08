package dev.arctic.heatmap;

import dev.arctic.heatmap.commands.CommandManager;
import dev.arctic.heatmap.commands.TabComplete;
import dev.arctic.heatmap.listeners.HeatmapRenderEventListener;
import dev.arctic.heatmap.listeners.PlayerMoveEventListener;
import dev.arctic.heatmap.utility.ConfigManager;
import dev.arctic.heatmap.utility.DataManagement;
import dev.arctic.heatmap.utility.HeatmapManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public final class Heatmap extends JavaPlugin {

    public static Heatmap plugin;
    public static int scalar;
    public static DataManagement dataManagement;
    public static int mapPluginStatus = 0; // 0 = no map plugins, 1 = Squaremap, 2 = Pl3xmap, 3 = Bluemap, 4 = Dynmap

    @Override
    public void onEnable() {
        plugin = this;
        ConfigManager.ensureConfig();
        scalar = getConfig().getInt("scalar");

        // Load heatmaps and commands setup
        dataManagement = new DataManagement();
        HeatmapManager.loadHeatmaps();

        getServer().getPluginManager().registerEvents(new HeatmapRenderEventListener(), this);
        Objects.requireNonNull(getCommand("heatmap")).setExecutor(new CommandManager());
        Objects.requireNonNull(getCommand("heatmap")).setTabCompleter(new TabComplete());

        //listeners
        final PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerMoveEventListener(), this);

        // Map plugin check
        checkMapPlugin(pm);
        plugin.getLogger().log(Level.WARNING, "Map Plugin Status: " + mapPluginStatus);

        getLogger().info("Heatmap plugin loaded and ready.");
    }

    private void checkMapPlugin(PluginManager pm) {
        if (pm.getPlugin("Squaremap") != null && pm.isPluginEnabled("Squaremap")) {
            mapPluginStatus = 1;
        } else if (pm.getPlugin("Pl3xmap") != null && pm.isPluginEnabled("Pl3xmap") ||
                pm.getPlugin("Dynmap") != null && pm.isPluginEnabled("Dynmap")) {
            mapPluginStatus = 2;
        } else if (pm.getPlugin("BlueMap") != null && pm.isPluginEnabled("BlueMap")) {
            mapPluginStatus = 3;
        } else if(pm.getPlugin("Dynmap") != null && pm.isPluginEnabled("Dynmap")){
            mapPluginStatus = 4;
        }
    }

    @Override
    public void onDisable() {
        DataManagement.saveHeatmapsSync(HeatmapManager.heatmaps);
        getLogger().info("Heatmap plugin shutdown.");
    }
}
