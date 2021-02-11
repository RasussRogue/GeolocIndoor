package com.arxit.geolocindoor.parsing;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Generated;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Generated
public class ParserApplication {

    private final static Logger LOG = Logger.getLogger(ParserApplication.class.getName());

    public static void main(String[] args) throws IOException, SQLException {

        try(var lines = Files.lines(Path.of("../graphedges.txt"))) {

            var entries = lines
                    .map(line -> line.split(" "))
                    .collect(Collectors.toList());

            var configOptions = new HikariConfig();
            configOptions.setJdbcUrl("jdbc:postgresql://gi.guilhemallaman.net:5432/geoloc_indoor");
            configOptions.setUsername("postgres");
            configOptions.setPassword("geolocindoor666");

            try (var ds = new HikariDataSource(configOptions); var connection = ds.getConnection()) {
                connection.setAutoCommit(false);

                try (PreparedStatement psDeleteAllEdges = connection.prepareStatement("delete from graphedge;");
                     PreparedStatement updateStatement = connection.prepareStatement("update graphedge\n" +
                             "    set value = CASE WHEN s.floor_id = e.floor_id THEN ST_distance(ST_Transform(s.geom, 2154), ST_Transform(e.geom, 2154)) ELSE 4 END\n" +
                             "    from poi s, poi e\n" +
                             "    where graphedge.start_poi = s.id and graphedge.end_poi = e.id;");
                ) {
                    //Delete existing edges
                    psDeleteAllEdges.executeUpdate();

                    //Add edges
                    int id = 0;
                    for (var entry : entries) {
                        try (PreparedStatement psAddAllEdges = connection.prepareStatement("insert into graphedge (id, building_id, start_poi, end_poi) values (" + (id++) + ", 0, " + entry[0] + ",  " + entry[1] + ");")) {
                            psAddAllEdges.executeUpdate();
                        }
                    }

                    //Update distances
                    updateStatement.executeUpdate();

                    connection.commit();

                    LOG.info("DONE !");

                }
            } catch (SQLException e) {
                throw new SQLException(e);
            }

        }
    }
}