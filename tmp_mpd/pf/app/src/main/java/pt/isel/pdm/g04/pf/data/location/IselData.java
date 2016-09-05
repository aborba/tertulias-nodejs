/*
package pt.isel.pdm.g04.pf.data.location;

import java.util.Collection;
import java.util.LinkedList;

import pt.isel.pdm.g04.pf.helpers.Logger;

public class IselData {
    public static Geo2DPlant getIsel() {
        Collection<Geo2DPolygon> buildings = new LinkedList<>();
        buildings.add(new Geo2DPolygon("Centro de Cálculo", new Geo2DPoint[] {
                new Geo2DPoint(38.75568, -9.114447),
                new Geo2DPoint(38.755678, -9.114877),
                new Geo2DPoint(38.755562, -9.114871),
                new Geo2DPoint(38.755568, -9.114444)
        }));
        buildings.add(new Geo2DPolygon("Edificio A - Servicos Administrativos", new Geo2DPoint[] {
                new Geo2DPoint(38.756562, -9.115635),
                new Geo2DPoint(38.756657, -9.115959),
                new Geo2DPoint(38.756165, -9.116199),
                new Geo2DPoint(38.756064, -9.115878)
        }));
        buildings.add(new Geo2DPolygon("Edificio C - Engenharia Civil", new Geo2DPoint[] {
                new Geo2DPoint(38.756394, -9.115139),
                new Geo2DPoint(38.756503, -9.115488),
                new Geo2DPoint(38.755934, -9.115777),
                new Geo2DPoint(38.755830, -9.115413)
        }));
        buildings.add(new Geo2DPolygon("Edificio E - Especialidades", new Geo2DPoint[] {
                new Geo2DPoint(38.756859, -9.116621),
                new Geo2DPoint(38.757108, -9.117443),
                new Geo2DPoint(38.756598, -9.117690),
                new Geo2DPoint(38.756354, -9.116856)
        }));
        buildings.add(new Geo2DPolygon("Edificio F - Engenharia DEEA e DEETC", new Geo2DPoint[] {
                new Geo2DPoint(38.755635, -9.115352),
                new Geo2DPoint(38.755719, -9.115506),
                new Geo2DPoint(38.755810, -9.115803),
                new Geo2DPoint(38.755251, -9.116084),
                new Geo2DPoint(38.755404, -9.115574),
                new Geo2DPoint(38.755579, -9.115649),
                new Geo2DPoint(38.755509, -9.115416)
        }));
        buildings.add(new Geo2DPolygon("Edificio G - Generalidades", new Geo2DPoint[] {
                new Geo2DPoint(38.755881, -9.115995),
                new Geo2DPoint(38.756062, -9.116625),
                new Geo2DPoint(38.755579, -9.116881),
                new Geo2DPoint(38.755381, -9.116233)
        }));
        buildings.add(new Geo2DPolygon("Edificio L - Pavilhao do Estudante", new Geo2DPoint[]{
                new Geo2DPoint(38.755587, -9.117465),
                new Geo2DPoint(38.755746, -9.118009),
                new Geo2DPoint(38.755589, -9.118041),
                new Geo2DPoint(38.755428, -9.117543)
        }));
        buildings.add(new Geo2DPolygon("Edificio M - Engenharia Mecanica", new Geo2DPoint[] {
                new Geo2DPoint(38.756097, -9.116999),
                new Geo2DPoint(38.756256, -9.117540),
                new Geo2DPoint(38.755857, -9.117736),
                new Geo2DPoint(38.755680, -9.117204)
        }));
        buildings.add(new Geo2DPolygon("Edificio P", new Geo2DPoint[]{
                new Geo2DPoint(38.756435, -9.116175),
                new Geo2DPoint(38.756503, -9.116407),
                new Geo2DPoint(38.756376, -9.116477),
                new Geo2DPoint(38.756474, -9.116799),
                new Geo2DPoint(38.756352, -9.116855),
                new Geo2DPoint(38.756393, -9.117016),
                new Geo2DPoint(38.756156, -9.117128),
                new Geo2DPoint(38.756107, -9.116977),
                new Geo2DPoint(38.756064, -9.116996),
                new Geo2DPoint(38.755964, -9.116689),
                new Geo2DPoint(38.756255, -9.116529),
                new Geo2DPoint(38.756184, -9.116300)
        }));
        buildings.add(new Geo2DPolygon("Complexo Aqui Estuda-se", new Geo2DPoint[]{
                new Geo2DPoint(38.755670, -9.115319),
                new Geo2DPoint(38.755451, -9.115296),
                new Geo2DPoint(38.755491, -9.115059),
                new Geo2DPoint(38.755623, -9.115091),
                new Geo2DPoint(38.755608, -9.115200),
                new Geo2DPoint(38.755687, -9.115225)
        }));
        buildings.add(new Geo2DPolygon("Residencial M Beatriz", new Geo2DPoint[]{
                new Geo2DPoint(38.757089, -9.117620),
                new Geo2DPoint(38.757230, -9.118084),
                new Geo2DPoint(38.756943, -9.118228),
                new Geo2DPoint(38.756788, -9.117770)
        }));

        Geo2DPlant isel = new Geo2DPlant(
                new Geo2DPolygon("ISEL - exterior", new Geo2DPoint[] {
                        new Geo2DPoint(38.756228, -9.113492),
                        new Geo2DPoint(38.757696, -9.118628),
                        new Geo2DPoint(38.755264, -9.118067),
                        new Geo2DPoint(38.755037, -9.116120),
                        new Geo2DPoint(38.755538, -9.113825)
                }), buildings);

        Geo2DPolygon polygon = isel.getPolygon(new Geo2DPoint(38.757177, -9.117033)); // Estacionamento norte
        Logger.i("Estacionamento norte " + polygon == null ? "not found" : polygon.name);

        polygon = isel.getPolygon(new Geo2DPoint(38.757000, -9.117923));
        Logger.i("Residencial " + polygon == null ? "not found" : polygon.name + (polygon.isPlant ? " - Plant" : ""));

        polygon = isel.getPolygon(new Geo2DPoint(38.756682, -9.117146));
        Logger.i("Ed E " + polygon == null ? "not found" : polygon.name + (polygon.isPlant ? " - Plant" : ""));

        polygon = isel.getPolygon(new Geo2DPoint(38.756208, -9.116745));
        Logger.i("Ed P " + polygon == null ? "not found" : polygon.name + (polygon.isPlant ? " - Plant" : ""));

        polygon = isel.getPolygon(new Geo2DPoint(38.756345, -9.116327));
        Logger.i("Ed P " + polygon == null ? "not found" : polygon.name + (polygon.isPlant ? " - Plant" : ""));

        polygon = isel.getPolygon(new Geo2DPoint(38.756141, -9.115458));
        Logger.i("Ed C " + polygon == null ? "not found" : polygon.name + (polygon.isPlant ? " - Plant" : ""));

        polygon = isel.getPolygon(new Geo2DPoint(38.755613, -9.114643));
        Logger.i("Ed CC " + polygon == null ? "not found" : polygon.name + (polygon.isPlant ? " - Plant" : ""));

        polygon = isel.getPolygon(new Geo2DPoint(38.755652, -9.115281));
        Logger.i("Complexo AE " + polygon == null ? "not found" : polygon.name + (polygon.isPlant ? " - Plant" : ""));

        polygon = isel.getPolygon(new Geo2DPoint(38.755343, -9.115960));
        Logger.i("Ed F " + polygon == null ? "not found" : polygon.name + (polygon.isPlant ? " - Plant" : ""));

        polygon = isel.getPolygon(new Geo2DPoint(38.755541, -9.115595));
        Logger.i("ISEL (buraco Ed F) " + polygon == null ? "not found" : polygon.name + (polygon.isPlant ? " - Plant" : ""));

        polygon = isel.getPolygon(new Geo2DPoint(38.755937, -9.113943));
        Logger.i("ISEL (Estacionamento Sul) " + polygon == null ? "not found" : polygon.name + (polygon.isPlant ? " - Plant" : ""));

        return isel;
    }
}
*/
