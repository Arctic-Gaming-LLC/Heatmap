package dev.arctic.heatmap.objects;

import java.awt.Color;

public class SquareZone {
    private final double[][] coordinates; // [[minX, minZ], [maxX, maxZ]]
    private final Color color;

    public SquareZone(double minX, double minZ, double maxX, double maxZ, Color color) {
        this.coordinates = new double[][]{{minX, minZ}, {maxX, maxZ}};
        this.color = color;
    }

    public double[][] getCoordinates() {
        return coordinates;
    }

    public Color getColor() {
        return color;
    }
}
