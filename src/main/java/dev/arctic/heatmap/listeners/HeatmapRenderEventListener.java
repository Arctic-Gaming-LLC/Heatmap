package dev.arctic.heatmap.listeners;

import dev.arctic.heatmap.events.HeatmapRenderEvent;
import dev.arctic.heatmap.squaremap.SquaremapRender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HeatmapRenderEventListener implements Listener {

    @EventHandler
    public void onHeatmapRender(HeatmapRenderEvent event) {
        if ("Squaremap".equals(event.getRenderType())) {
            new SquaremapRender(event.getHeatmapObject(), event.getZones());
        }
    }
}
