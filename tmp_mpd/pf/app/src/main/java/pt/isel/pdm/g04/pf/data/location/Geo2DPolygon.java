/*
package pt.isel.pdm.g04.pf.data.location;

import android.location.Location;

public class Geo2DPolygon {
    public final String name;
    public final Geo2DPoint[] vertexes;
    public final boolean isPlant;

    public Geo2DPolygon(String name, Geo2DPoint[] vertexes) {
        this(name, vertexes, false);
    }

    public Geo2DPolygon(String name, Geo2DPoint[] vertexes, boolean isPlant) {
        this.name = name;
        this.vertexes = vertexes;
        this.isPlant = isPlant;
    }

    public boolean contains(Location location) {
        return contains(new Geo2DPoint(location.getLatitude(), location.getLongitude()));
    }

    public boolean contains(Geo2DPoint point) {
        int crossesCount = 0;
        for (int i = 0; i < vertexes.length - 1; i++)
            if (pointSegmentCross(point, vertexes[i], vertexes[i + 1])) crossesCount++;
        int last = vertexes.length - 1;
        if (pointSegmentCross(point, vertexes[last], vertexes[0])) crossesCount ++;
        return crossesCount % 2 == 1;
    }

    public static boolean pointSegmentCross(Geo2DPoint point, Geo2DPoint startPoint, Geo2DPoint endPoint) {
        return lineSegmentsCross(startPoint, endPoint, new Geo2DPoint(0.0, 0.0), point);
    }

    public static boolean lineSegmentsCross(Geo2DPoint segment1Start, Geo2DPoint segment1End, Geo2DPoint segment2Start, Geo2DPoint segment2End) {
        return lineSegmentsCross(segment1Start.latitude, segment1Start.longitude, segment1End.latitude, segment1End.longitude,
                segment2Start.latitude, segment2Start.longitude, segment2End.latitude, segment2End.longitude);
    }

    public static boolean lineSegmentsCross(double s1x1, double s1y1, double s1x2, double s1y2, double s2x1, double s2y1, double s2x2, double s2y2) {
        double denominator = (s2y2 - s2y1) * (s1x2 - s1x1) - (s2x2 - s2x1) * (s1y2 - s1y1);
        if (denominator == 0.0) return false; // parallel
        double r1 = ((s2x2 - s2x1) * (s1y1 - s2y1) - (s2y2 - s2y1) * (s1x1 - s2x1)) / denominator;
        double r2 = ((s1x2 - s1x1) * (s1y1 - s2y1) - (s1y2 - s1y1) * (s1x1 - s2x1)) / denominator;
        if (r1 == 0) return false;
        if (r1 >= 0.0f && r1 <= 1.0f && r2 >= 0.0f && r2 <= 1.0f) return true; // cross
        return false; // no cross
    }

}
*/
