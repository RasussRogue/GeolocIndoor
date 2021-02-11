package com.arxit.geolocindoor.common.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;

import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "floor")
@NamedQueries({
        @NamedQuery(name = "Floor.findByBuilding", query = "SELECT f FROM Floor f WHERE f.building = :building")
})
@SequenceGenerator(name = "gen_floor")
public class Floor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_floor")
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "level", nullable = false)
    private int level;

    @ManyToOne
    @JoinColumn(name = "building_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private Building building;

    @OneToMany(mappedBy = "floor")
    @JsonManagedReference
    private List<POI> pois;

    @OneToMany(mappedBy = "floor")
    @JsonManagedReference
    private List<Beacon> beacons;

    @OneToMany(mappedBy = "floor")
    private List<FloorBound> bounds;

    @OneToMany(mappedBy = "floor")
    private List<FloorWalls> walls;

    public Floor() {
        pois = new ArrayList<>();
        beacons = new ArrayList<>();
        bounds = new ArrayList<>();
        walls = new ArrayList<>();
    }

    public Floor(String name) {
        this();
        this.name = name;
    }

    public Floor(String name, int level, Building building) {
        this();
        this.name = name;
        this.level = level;
        this.building = building;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public List<POI> getPois() {
        return pois;
    }

    public void setPois(List<POI> pois) {
        this.pois = pois;
    }

    public List<Beacon> getBeacons() {
        return beacons;
    }

    public void setBeacons(List<Beacon> beacons) {
        this.beacons = beacons;
    }

    public List<FloorBound> getBounds() {
        return bounds;
    }

    public void setBounds(List<FloorBound> bounds) {
        this.bounds = bounds;
    }

    public List<FloorWalls> getWalls() {
        return walls;
    }

    public void setWalls(List<FloorWalls> walls) {
        this.walls = walls;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Floor floor = (Floor) o;
        return id == floor.id &&
                level == floor.level &&
                Objects.equals(name, floor.name) &&
                Objects.equals(pois, floor.pois) &&
                Objects.equals(beacons, floor.beacons) &&
                Objects.equals(bounds, floor.bounds) &&
                Objects.equals(walls, floor.walls);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, level,
                this.pois.stream().mapToInt(POI::hashCode).sum(),
                this.beacons.stream().mapToInt(Beacon::hashCode).sum(),
                this.bounds.stream().mapToInt(FloorBound::hashCode).sum(),
                this.walls.stream().mapToInt(FloorWalls::hashCode).sum());
    }
}
