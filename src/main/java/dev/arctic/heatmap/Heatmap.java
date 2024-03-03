package dev.arctic.heatmap;

import dev.arctic.heatmap.commands.CommandManager;
import dev.arctic.heatmap.commands.TabComplete;
import dev.arctic.heatmap.listeners.PlayerMoveEventListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public final class Heatmap extends JavaPlugin {

    public static Heatmap plugin;
    public static int scalar;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        plugin.saveConfig();
        plugin.saveDefaultConfig();

        scalar = plugin.getConfig().getInt("scalar");

        plugin.getLogger().log(Level.INFO, "Heatmap warmed up!");
        HeatmapManager.loadHeatmaps();
        plugin.getLogger().log(Level.INFO, "Heatmaps loaded");


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
        HeatmapManager.saveHeatmaps();
        plugin.getLogger().log(Level.WARNING, "Plugin closed down!");
    }
}
