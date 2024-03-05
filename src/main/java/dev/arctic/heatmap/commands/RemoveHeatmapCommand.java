package dev.arctic.heatmap.commands;

import dev.arctic.heatmap.utility.HeatmapManager;

import java.util.logging.Level;

import static dev.arctic.heatmap.Heatmap.dataManagement;
import static dev.arctic.heatmap.Heatmap.plugin;

public class RemoveHeatmapCommand {
    public boolean execute(String regionID) {

        if(HeatmapManager.heatmaps.containsKey(regionID)){
            HeatmapManager.removeHeatmap(regionID);
            dataManagement.removeHeatmap(regionID);
            plugin.getLogger().log(Level.WARNING, "Heatmap for region \"" + regionID + "\" was removed.");
            return true;
        }
        return false;
    }
}
