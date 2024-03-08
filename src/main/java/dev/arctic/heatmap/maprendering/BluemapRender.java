package dev.arctic.heatmap.maprendering;

import com.flowpowered.math.vector.Vector2d;
import de.bluecolored.bluemap.api.math.Shape;
import dev.arctic.heatmap.objects.HeatmapObject;
import dev.arctic.heatmap.objects.SquareZone;
import dev.arctic.heatmap.utility.WorldGuardHelper;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.markers.ShapeMarker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.math.Color;

import static dev.arctic.heatmap.Heatmap.plugin;

public class BluemapRender {
    private final HeatmapObject heatmapObject;
    private final List<SquareZone> zones;

    public BluemapRender(HeatmapObject heatmapObject, List<SquareZone> zones) {
        this.heatmapObject = heatmapObject;
        this.zones = zones;
        renderZones();
    }

    private void renderZones() {
        World world = WorldGuardHelper.findWorldByRegionId(heatmapObject.getRegionID());
        if (world == null) {
            plugin.getLogger().warning("No world found for " + heatmapObject.getRegionID());
            return;
        }

        BlueMapAPI.getInstance().ifPresent(api -> {
            api.getWorld(world).ifPresent(blueWorld -> {

                MarkerSet set = new MarkerSet("Heatmap");
                set.setLabel("Heatmap");
                set.setToggleable(true);

                zones.forEach(zone -> {
                    Color color = convertToBluemapColor(zone.getColor());
                    ShapeMarker shapeMarker = createShapeMarkerFromZone(zone, color);
                    set.put("heatmap" + Arrays.deepToString(zone.getCoordinates()), shapeMarker);
                });

                blueWorld.getMaps().forEach(map -> {
                    map.getMarkerSets().put("heatmap", set);
                    plugin.getLogger().log(Level.WARNING, "Heatmap Rendered!");
                });
            });
        });
    }


    private ShapeMarker createShapeMarkerFromZone(SquareZone zone, Color color) {
        List<Vector2d> points = new ArrayList<>();
        double[][] coords = zone.getCoordinates();
        points.add(new Vector2d(coords[0][0], coords[0][1])); // Min
        points.add(new Vector2d(coords[1][0], coords[0][1])); // MaxX, MinZ
        points.add(new Vector2d(coords[1][0], coords[1][1])); // Max
        points.add(new Vector2d(coords[0][0], coords[1][1])); // MinX, MaxZ
        Shape shape = new Shape(points);

        // Determine appropriate Y value
        float yValue = findYValue(coords);

        ShapeMarker shapeMarker = ShapeMarker.builder()
                .label("Heatmap Zone")
                .shape(shape, yValue)
                .lineColor(color)
                .fillColor(color)
                .build();

        return shapeMarker;
    }

    private float findYValue(double[][] zoneCoords) {
        double totalY = 0;
        int count = 0;

        for (Location location : heatmapObject.getGeneratedHeatmap().keySet()) {
            if (location.getX() >= zoneCoords[0][0] && location.getX() <= zoneCoords[1][0] &&
                    location.getZ() >= zoneCoords[0][1] && location.getZ() <= zoneCoords[1][1]) {
                totalY += location.getY();
                count++;
            }
        }

        // Calculate the average Y value if there are matching locations
        return count > 0 ? (float) (totalY / count) : 256; // Use 256 as a fallback Y value
    }

    private Color convertToBluemapColor(java.awt.Color awtColor) {
        return new Color(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue(), awtColor.getAlpha());
    }
}
