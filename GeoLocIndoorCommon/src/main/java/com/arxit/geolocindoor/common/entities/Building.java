package com.arxit.geolocindoor.common.entities;

import com.arxit.geolocindoor.common.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.HipsterGraph;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Entity
@Table(name = "building")
@SequenceGenerator(name="gen_building")
public class Building implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_building")
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Floor> floors;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<GraphEdge> edges;

    public Building(){
        this.floors = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public Building(long id, String name, List<Floor> floors, List<GraphEdge> edges) {
        this.id = id;
        this.name = name;
        this.floors = floors;
        this.edges = edges;
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

    public List<Floor> getFloors() {
        return floors;
    }

    public void setFloors(List<Floor> floors) {
        this.floors = floors;
    }

    public List<GraphEdge> getEdges() {
        return edges;
    }

    public void setEdges(List<GraphEdge> edges) {
        this.edges = edges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Building building = (Building) o;
        return id == building.id &&
                Objects.equals(name, building.name) &&
                Objects.equals(floors, building.floors) &&
                Objects.equals(edges, building.edges);
    }

    @Override
    public int hashCode() {
        //return Objects.hash(id, name, this.floors, this.edges);
        return Objects.hash(id, name,
                this.floors.stream().mapToInt(Floor::hashCode).sum(),
                this.edges.stream().mapToInt(GraphEdge::hashCode).sum());
    }

    @JsonIgnore
    public Map<String, Beacon> getBeacons(){
        return Collections.unmodifiableMap(
            this.floors.stream().flatMap(f -> f.getBeacons().stream())
                .collect(Collectors.toMap(Beacon::getUuid, Function.identity())));
    }

    public Optional<Floor> getFloorByLevel(int floorLevel){
        return this.floors.stream().filter(f -> f.getLevel() == floorLevel).findFirst();
    }

    @JsonIgnore
    public Optional<POI> getPoiById(long id){
        return this.floors.stream().flatMap(f -> f.getPois().stream()).filter(poi -> poi.getId() == id).findFirst();
    }

    @JsonIgnore
    public HipsterGraph<POI, Double> getGraph(){

        Map<Long, Floor> map = new HashMap<>();
        this.floors.forEach(floor -> floor.getPois().forEach(poi -> map.put(poi.getId(), floor)));

        GraphBuilder<POI,Double> graphBuilder = GraphBuilder.create();
        this.edges.forEach(edge -> {
            edge.getStart().setFloor(map.get(edge.getStart().getId()));
            edge.getEnd().setFloor(map.get(edge.getEnd().getId()));
            graphBuilder.connect(edge.getStart()).to(edge.getEnd()).withEdge(edge.getValue());
        });
        return graphBuilder.createUndirectedGraph();
    }

    @JsonIgnore
    public HipsterGraph<POI, Double> getAccessibilityGraph(){

        Map<Long, Floor> map = new HashMap<>();
        this.floors.forEach(floor -> floor.getPois().forEach(poi -> map.put(poi.getId(), floor)));

        GraphBuilder<POI,Double> graphBuilder = GraphBuilder.create();
        this.edges.forEach(edge -> {
            edge.getStart().setFloor(map.get(edge.getStart().getId()));
            edge.getEnd().setFloor(map.get(edge.getEnd().getId()));
            if (!(edge.getStart() instanceof VisiblePOI && ((VisiblePOI)edge.getStart()).getType().equals("stairs") &&
                    edge.getEnd() instanceof VisiblePOI && ((VisiblePOI)edge.getEnd()).getType().equals("stairs"))){
                graphBuilder.connect(edge.getStart()).to(edge.getEnd()).withEdge(edge.getValue());
            }
        });
        return graphBuilder.createUndirectedGraph();
    }

    public List<VisiblePOI> getPlacesMatching(@NonNull String request){
        String matching = StringUtils.unaccent(request);
        return this.floors.stream()
                .flatMap(floor -> floor.getPois().stream())
                .filter(VisiblePOI.class::isInstance)
                .map(VisiblePOI.class::cast)
                .filter(p -> p.matches(matching))
                .collect(Collectors.toList());
    }

    public Optional<VisiblePOI> getPoiByName(String name){
        return this.getPlacesMatching(name).stream().findFirst();
    }

}
