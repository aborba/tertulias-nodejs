/*
package pt.isel.pdm.g04.pf.data.location;

import android.location.Location;

import java.util.Collection;

public class Geo2DPlant {
    final Geo2DPolygon plant;
    final Collection<Geo2DPolygon> geo2DPolygons;

    public Geo2DPlant(Geo2DPolygon plant, Collection<Geo2DPolygon> geo2DPolygons) {
        this.plant = new Geo2DPolygon(plant.name, plant.vertexes, true);
        this.geo2DPolygons = geo2DPolygons;
    }

    public Geo2DPolygon getPolygon(Location location) {
        return getPolygon(new Geo2DPoint(location.getLatitude(), location.getLongitude()));
    }

    public Geo2DPolygon getPolygon(Geo2DPoint geo2DPoint) {
        for (Geo2DPolygon polygon : geo2DPolygons) if (polygon.contains(geo2DPoint)) return polygon;
        if (plant.contains(geo2DPoint)) return plant;
        return null;
    }
}
*/
