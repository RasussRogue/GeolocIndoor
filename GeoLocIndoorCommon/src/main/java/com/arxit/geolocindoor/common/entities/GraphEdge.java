package com.arxit.geolocindoor.common.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "graphedge")
@SequenceGenerator(name="gen_graphedge")
public class GraphEdge implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_graphedge")
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @ManyToOne
    @JoinColumn(name = "start_poi", referencedColumnName = "id", nullable = false)
    private POI start;

    @ManyToOne
    @JoinColumn(name = "end_poi", referencedColumnName = "id", nullable = false)
    private POI end;

    @Column(name = "value")
    private double value;

    @ManyToOne
    @JoinColumn(name = "building_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private Building building;

    public GraphEdge(){}

    public GraphEdge(POI start, POI end, double value, Building building){
        this.start = start;
        this.end = end;
        this.value = value;
        this.building = building;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public POI getStart() {
        return start;
    }

    public void setStart(POI start) {
        this.start = start;
    }

    public POI getEnd() {
        return end;
    }

    public void setEnd(POI end) {
        this.end = end;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphEdge graphEdge = (GraphEdge) o;
        return id == graphEdge.id &&
                Double.compare(graphEdge.value, value) == 0 &&
                Objects.equals(start, graphEdge.start) &&
                Objects.equals(end, graphEdge.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, start.getId(), end.getId(), value);
    }
}
