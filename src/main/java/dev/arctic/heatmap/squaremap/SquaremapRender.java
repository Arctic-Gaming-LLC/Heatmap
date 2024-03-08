package dev.arctic.heatmap.squaremap;

import dev.arctic.heatmap.objects.HeatmapObject;
import dev.arctic.heatmap.objects.SquareZone;
import dev.arctic.heatmap.utility.WorldGuardHelper;
import org.bukkit.World;
import xyz.jpenilla.squaremap.api.*;
import xyz.jpenilla.squaremap.api.marker.Marker;
import xyz.jpenilla.squaremap.api.marker.MarkerOptions;
import xyz.jpenilla.squaremap.api.marker.Rectangle;

import java.awt.*;
import java.util.List;

import static dev.arctic.heatmap.Heatmap.plugin;

public class SquaremapRender {
    private final HeatmapObject heatmapObject;
    private final List<SquareZone> zones;

    public SquaremapRender(HeatmapObject heatmapObject, List<SquareZone> zones) {
        this.heatmapObject = heatmapObject;
        this.zones = zones;
        renderZones();
    }

    private void renderZones() {
        World world = WorldGuardHelper.findWorldByRegionId(heatmapObject.getRegionID());
        if (world == null) {
            plugin.getLogger().warning("No world found for region ID: " + heatmapObject.getRegionID());
            return;
        }

        Squaremap api = SquaremapProvider.get();
        MapWorld mapWorld = api.getWorldIfEnabled(BukkitAdapter.worldIdentifier(world)).orElse(null);
        if (mapWorld == null) {
            plugin.getLogger().warning("Squaremap does not have the world enabled: " + world.getName());
            return;
        }

        Key layerKey = Key.of("heatmap_layer_" + heatmapObject.getRegionID());
        SimpleLayerProvider provider = SimpleLayerProvider.builder("Heatmap Layer")
                .showControls(true)
                .defaultHidden(false)
                .layerPriority(5)
                .zIndex(250)
                .build();
        mapWorld.layerRegistry().register(layerKey, provider);

        for (SquareZone zone : zones) {
            // Construct a unique key for each zone based on "hits", "x", and "z"
            String zoneKey = "zone_" + zone.getMinPoint().x() + "_" + zone.getMinPoint().z();
            Key markerKey = Key.of(zoneKey);

            MarkerOptions options = MarkerOptions.builder()
                    .fillColor(zone.getColor())
                    .fillOpacity(0.5)
                    .strokeColor(Color.BLACK)
                    .strokeWeight(1)
                    .build();

            Rectangle rectangle = Marker.rectangle(zone.getMinPoint(), zone.getMaxPoint());
            rectangle.markerOptions(options);
            provider.addMarker(markerKey, rectangle);
        }
    }
}
