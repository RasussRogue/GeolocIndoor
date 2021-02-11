package com.arxit.geolocindoor.common.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Formula;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "beacon")
public class Beacon implements Pointable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name="gen_beacon", sequenceName = "seq_beacon")
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "height", nullable = false)
    private double height;

    @ManyToOne
    @JoinColumn(name = "floor_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private Floor floor;

    @JsonIgnore
    @Column(name = "geom", columnDefinition = "Geometry(Point, 4326)", nullable = false)
    private Point geom;

    @Formula("ST_ASTEXT(geom)")
    private String wkt;

    public Beacon(){}

    public Beacon(String uuid, double height, Floor floor, Point geom){
        this.uuid = uuid;
        this.height = height;
        this.floor = floor;
        this.geom = geom;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    public Point getGeom() {
        return geom;
    }

    public void setGeom(Point geom) {
        this.geom = geom;
    }

    @Override
    public String getWkt() {
        return wkt;
    }

    public void setWkt(String wkt) {
        this.wkt = wkt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Beacon beacon = (Beacon) o;
        return id == beacon.id &&
                Objects.equals(uuid, beacon.uuid) &&
                Objects.equals(height, beacon.height) &&
                Objects.equals(wkt, beacon.wkt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid, height, wkt);
    }

    @Override
    public Point getGeometry() {
        if (this.geom != null){
            return this.geom;
        }
        try {
            return (Point) (new WKTReader().read(this.wkt));
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }
}
