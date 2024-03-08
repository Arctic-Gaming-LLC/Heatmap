package dev.arctic.heatmap.objects;

import xyz.jpenilla.squaremap.api.Point;
import java.awt.Color;

public class SquareZone {
    private final Point minPoint;
    private final Point maxPoint;
    private final Color color; // Now storing color directly

    public SquareZone(Point minPoint, Point maxPoint, Color color) {
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
        this.color = color;
    }

    public Point getMinPoint() {
        return minPoint;
    }

    public Point getMaxPoint() {
        return maxPoint;
    }

    public Color getColor() {
        return color;
    }
}
