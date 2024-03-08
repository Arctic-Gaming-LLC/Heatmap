package dev.arctic.heatmap.listeners;

import dev.arctic.heatmap.events.HeatmapRenderEvent;
import dev.arctic.heatmap.maprendering.BluemapRender;
import dev.arctic.heatmap.maprendering.SquaremapRender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.logging.Level;

import static dev.arctic.heatmap.Heatmap.plugin;

public class HeatmapRenderEventListener implements Listener {

    @EventHandler
    public void onHeatmapRender(HeatmapRenderEvent event) {
        switch (event.getRenderType()) {

            case "squaremap":
                new SquaremapRender(event.getHeatmapObject(), event.getZones());
                break;
            case "BlueMap":
                new BluemapRender(event.getHeatmapObject(), event.getZones());
                break;
            default:
                break;
        }
    }
}
