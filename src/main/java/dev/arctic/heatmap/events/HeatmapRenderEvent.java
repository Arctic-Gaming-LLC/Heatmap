package dev.arctic.heatmap.events;

import dev.arctic.heatmap.objects.HeatmapObject;
import dev.arctic.heatmap.objects.SquareZone;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class HeatmapRenderEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private HeatmapObject heatmapObject;
    private List<SquareZone> zones;
    private String renderType;

    public HeatmapRenderEvent(HeatmapObject heatmapObject, String renderType, List<SquareZone> zones) {
        this.heatmapObject = heatmapObject;
        this.renderType = renderType;
        this.zones = zones;
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
