package dev.arctic.heatmap.commands;

import dev.arctic.heatmap.visualization.GridZone;
import dev.arctic.heatmap.objects.HeatmapObject;
import dev.arctic.heatmap.utility.WorldGuardHelper;
import org.bukkit.World;

import static dev.arctic.heatmap.utility.HeatmapManager.heatmaps;

public class RenderHeatmapCommand {

    public void execute(String regionID) {
        for(HeatmapObject heatmap : heatmaps.values()){
            if(heatmap.getRegionID().equals(regionID)){
                World world = WorldGuardHelper.findWorldByRegionId(regionID);
                if(world != null){
                    new GridZone(heatmap).createGridZonesAsync();
                }
                return;
            }
        }
    }
}
