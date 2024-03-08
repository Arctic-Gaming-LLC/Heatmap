package dev.arctic.heatmap.events;

import dev.arctic.heatmap.objects.HeatmapObject;
import dev.arctic.heatmap.objects.SquareZone;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

import static dev.arctic.heatmap.Heatmap.mapPluginStatus;

public class HeatmapRenderEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private HeatmapObject heatmapObject;
    private List<SquareZone> zones;
    private String renderType;

    public HeatmapRenderEvent(HeatmapObject heatmapObject, List<SquareZone> zones) {
        this.heatmapObject = heatmapObject;
        this.zones = zones;

        switch (mapPluginStatus) {
            case 1:
                this.renderType = "squaremap";
                break;
            case 2:
                this.renderType = "pl3xmap";
                break;
            case 3:
                this.renderType = "BlueMap";
                break;
            case 4:
                this.renderType = "dynmap";
                break;
            default:
                this.renderType = "none";
        }
    }

    public HeatmapObject getHeatmapObject() {
        return heatmapObject;
    }

    public List<SquareZone> getZones() {
        return zones;
    }

    public String getRenderType() {
        return renderType;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
