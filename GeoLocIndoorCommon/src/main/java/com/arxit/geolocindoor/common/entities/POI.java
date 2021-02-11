package com.arxit.geolocindoor.common.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.hibernate.annotations.Formula;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "poi")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type_poi")
@DiscriminatorValue("basic")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "class")
@JsonSubTypes({
        @JsonSubTypes.Type(value = VisiblePOI.class)
})
@SequenceGenerator(name="gen_poi")
public class POI implements Pointable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_poi")
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @ManyToOne
    @JoinColumn(name = "floor_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private Floor floor;

    @JsonIgnore
    @Column(name = "geom", columnDefinition = "Geometry(Point, 4326)", nullable = false)
    private Point geom;

    @Formula("ST_ASTEXT(geom)")
    private String wkt;

    public POI(){}

    public POI(Floor floor, Point geom){
        this.floor = floor;
        this.geom = geom;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
        POI poi = (POI) o;
        return id == poi.id &&
                Objects.equals(wkt, poi.wkt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, wkt);
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
