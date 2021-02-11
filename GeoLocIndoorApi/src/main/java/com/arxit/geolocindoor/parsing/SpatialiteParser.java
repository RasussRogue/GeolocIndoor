package com.arxit.geolocindoor.parsing;

import com.arxit.geolocindoor.common.entities.*;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.sqlite.SQLiteConfig;

import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class SpatialiteParser {

    private final WKTReader wktReader;
    private final Building building;
    private final Map<Long, Floor> floors;
    private final Map<Long, POI> pois;

    private static final int WGS84 = 4326;
    private static final Logger logger = Logger.getLogger(SpatialiteParser.class.getName());

    public SpatialiteParser() {
        this.wktReader = new WKTReader();
        this.building = new Building();
        this.floors = new HashMap<>();
        this.pois = new HashMap<>();
    }

    public Building getBuilding(Path filePath, String buildingName){

        SQLiteConfig config = new SQLiteConfig();
        config.enableLoadExtension(true);

        try(var connection = DriverManager.getConnection("jdbc:sqlite:" + filePath.toString(), config.toProperties());
                var statement = connection.createStatement()){

            //import spatialite extension
            statement.setQueryTimeout(30);
            statement.execute("select load_extension('mod_spatialite')");

            logger.info("Processing floors ...");
            this.processFloors(statement.executeQuery("select * from floor"));
            logger.info("Processing floor bounds ...");
            this.processFloorBounds(statement.executeQuery("select floor_id, ST_AsText(geom) as geom_wkt from floorbound"));
            logger.info("Processing floor walls ...");
            this.processFloorWalls(statement.executeQuery("select floor_id, ST_AsText(geom) as geom_wkt from floorwalls"));
            logger.info("Processing beacons ...");
            this.processBeacons(statement.executeQuery("select uuid, height, floor_id, ST_AsText(geom) as geom_wkt from beacon"));
            logger.info("Processing POIs ...");
            this.processPois(statement.executeQuery("select id, type_poi, title, type, floor_id, ST_AsText(geom) as geom_wkt from poi"));
            logger.info("Processing graph edges ...");
            this.processEdges(statement.executeQuery("select * from graphedge"));

            this.building.setName(buildingName);
            return building;
        } catch (SQLException | ParseException e) {
            throw new SpatialiteParsingException(e);
        }
    }

    private void processFloors(ResultSet rs) throws SQLException {
        while (rs.next()){
            var floor = new Floor(rs.getString("name"), rs.getInt("level"), this.building);
            this.building.getFloors().add(floor);
            this.floors.put(rs.getLong("id"), floor);
        }
    }

    private void processFloorBounds(ResultSet rs) throws SQLException, ParseException {
        while (rs.next()){
            var geom = (MultiPolygon) (this.wktReader.read(rs.getString("geom_wkt")));
            geom.setSRID(WGS84);
            var floor = this.floors.get(rs.getLong("floor_id"));
            floor.getBounds().add(new FloorBound(geom, floor));
        }
    }

    private void processFloorWalls(ResultSet rs) throws SQLException, ParseException {
        while (rs.next()){
            var geom = (MultiLineString) (this.wktReader.read(rs.getString("geom_wkt")));
            geom.setSRID(WGS84);
            var floor = this.floors.get(rs.getLong("floor_id"));
            floor.getWalls().add(new FloorWalls(geom, floor));
        }
    }

    private void processBeacons(ResultSet rs) throws SQLException, ParseException {
        while(rs.next()){
            var geom = (Point) (this.wktReader.read(rs.getString("geom_wkt")));
            geom.setSRID(WGS84);
            var floor = this.floors.get(rs.getLong("floor_id"));
            var beacon = new Beacon(rs.getString("uuid"), rs.getDouble("height"), floor, geom);
            floor.getBeacons().add(beacon);
        }
    }

    private void processPois(ResultSet rs) throws SQLException, ParseException {
        while(rs.next()){
            var geom = (Point) (this.wktReader.read(rs.getString("geom_wkt")));
            geom.setSRID(WGS84);
            var floor = this.floors.get(rs.getLong("floor_id"));
            switch (rs.getString("type_poi")){
                case "basic":
                    var poi = new POI(floor, geom);
                    floor.getPois().add(poi);
                    this.pois.put(rs.getLong("id"), poi);
                    break;
                case "visible":
                    var vpoi = new VisiblePOI(rs.getString("title"), rs.getString("type"), floor, geom);
                    floor.getPois().add(vpoi);
                    this.pois.put(rs.getLong("id"), vpoi);
                    break;
            }
        }
    }

    private void processEdges(ResultSet rs) throws SQLException {
        while(rs.next()){
            var startPoi = this.pois.get(rs.getLong("start_poi"));
            var endPoi = this.pois.get(rs.getLong("end_poi"));
            var edge = new GraphEdge(startPoi, endPoi, rs.getDouble("value"), this.building);
            this.building.getEdges().add(edge);
        }
    }
}
