package dev.arctic.heatmap;

import dev.arctic.heatmap.commands.CommandManager;
import dev.arctic.heatmap.commands.TabComplete;
import dev.arctic.heatmap.listeners.PlayerMoveEventListener;
import dev.arctic.heatmap.utility.ConfigManager;
import dev.arctic.heatmap.utility.DataManagement;
import dev.arctic.heatmap.utility.HeatmapManager;
import dev.arctic.heatmap.utility.StorageStrategy;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public final class Heatmap extends JavaPlugin {

    public static Heatmap plugin;
    public static int scalar;

    public static DataManagement dataManagement;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        ConfigManager.ensureConfig();

        // Load config values
        scalar = getConfig().getInt("scalar");

        dataManagement = new DataManagement();

        getLogger().info("Heatmap warmed up!");
        HeatmapManager.loadHeatmaps();
        getLogger().info("Heatmaps loaded");


        //implement commands
        Objects.requireNonNull(getCommand("heatmap")).setExecutor(new CommandManager());
        Objects.requireNonNull(getCommand("heatmap")).setTabCompleter(new TabComplete());


        final PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerMoveEventListener(), this);

        plugin.getLogger().log(Level.WARNING,"Plugin loaded and ready to map");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        DataManagement.saveHeatmapsSync(HeatmapManager.heatmaps);
        plugin.getLogger().log(Level.WARNING, "Plugin closed down!");
    }
}
