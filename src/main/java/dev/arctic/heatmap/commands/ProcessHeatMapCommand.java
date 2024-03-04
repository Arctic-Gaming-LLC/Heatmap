package dev.arctic.heatmap.commands;

import dev.arctic.heatmap.utility.HeatmapManager;
import dev.arctic.heatmap.objects.HeatmapObject;

import java.util.logging.Level;

import static dev.arctic.heatmap.Heatmap.plugin;

public class ProcessHeatMapCommand {
    public boolean execute(String regionID) {

        if(HeatmapManager.heatmaps.containsKey(regionID)){

            HeatmapObject heatmap = HeatmapManager.getHeatmap(regionID);
            heatmap.processData();
            heatmap.setTracking(false);
            HeatmapManager.updateHeatmap(regionID, heatmap);
            HeatmapManager.saveHeatmaps();
            return true;
        }
        plugin.getLogger().log(Level.WARNING, "There was not a valid heatmap for this region.");
        return false;
    }
}
