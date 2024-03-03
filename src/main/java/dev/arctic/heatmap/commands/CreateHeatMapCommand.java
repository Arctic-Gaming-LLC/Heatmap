package dev.arctic.heatmap.commands;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.arctic.heatmap.HeatmapManager;
import dev.arctic.heatmap.objects.HeatmapObject;

import java.util.logging.Level;

import static dev.arctic.heatmap.Heatmap.plugin;

public class CreateHeatMapCommand {

    public boolean execute(String regionID) {
        if(ProtectedRegion.isValidId(regionID)){
            HeatmapManager.addHeatmap(regionID,new HeatmapObject(regionID));
            return true;
        }
        plugin.getLogger().log(Level.WARNING, "Region ID was not valid!");
        return false;
    }
}
