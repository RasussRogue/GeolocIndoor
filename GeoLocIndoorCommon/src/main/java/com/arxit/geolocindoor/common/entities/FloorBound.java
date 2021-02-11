package com.arxit.geolocindoor.common.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "floorbound")
@SequenceGenerator(name="gen_floor_bound")
public class FloorBound implements Displayable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_floor_bound")
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @JsonIgnore
    @Column(name = "geom", columnDefinition = "Geometry(MultiPolygon, 4326)")
    @Type(type="jts_geometry")
    private MultiPolygon geom;

    @Formula("ST_ASTEXT(geom)")
    private String wkt;

    @OneToOne
    @JoinColumn(name = "floor_id", referencedColumnName = "id", nullable = false)
    private Floor floor;

    public FloorBound(){}

    public FloorBound(MultiPolygon geom, Floor floor) {
        this.geom = geom;
        this.floor = floor;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MultiPolygon getGeom() {
        return geom;
    }

    public void setGeom(MultiPolygon geom) {
        this.geom = geom;
    }

    @Override
    public String getWkt() {
        return wkt;
    }

    public void setWkt(String wkt) {
        this.wkt = wkt;
    }

    @JsonIgnore
    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    @Override
    public MultiPolygon getGeometry() {
        if (this.geom != null){
            return this.geom;
        }
        try {
            return (MultiPolygon) new WKTReader().read(this.wkt);
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FloorBound that = (FloorBound) o;
        return id == that.id &&
                Objects.equals(wkt, that.wkt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, wkt);
    }
}