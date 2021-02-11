package com.arxit.geolocindoor.common.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Formula;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "floorwalls")
@SequenceGenerator(name="gen_floorwalls")
public class FloorWalls implements Displayable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_floorwalls")
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @JsonIgnore
    @Column(name = "geom", columnDefinition = "Geometry(MultiLineString, 4326)")
    private MultiLineString geom;

    @Formula("ST_ASTEXT(geom)")
    private String wkt;

    @OneToOne
    @JoinColumn(name = "floor_id", referencedColumnName = "id", nullable = false)
    private Floor floor;

    public FloorWalls(){}

    public FloorWalls(MultiLineString geom, Floor floor){
        this.geom = geom;
        this.floor = floor;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MultiLineString getGeom() {
        return geom;
    }

    public void setGeom(MultiLineString geom) {
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
    public MultiLineString getGeometry() {
        if (this.geom != null){
            return this.geom;
        }
        try {
            return (MultiLineString) (new WKTReader().read(this.wkt));
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FloorWalls that = (FloorWalls) o;
        return id == that.id &&
                Objects.equals(wkt, that.wkt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, wkt);
    }


}
