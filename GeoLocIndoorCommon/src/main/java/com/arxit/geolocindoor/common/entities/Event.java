package com.arxit.geolocindoor.common.entities;

import com.arxit.geolocindoor.common.utils.StringUtils;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "event")
@NamedQueries({
        @NamedQuery(name = "Event.findByTitle", query = "SELECT e FROM Event e WHERE e.title = :title"),
        @NamedQuery(name = "Event.findByPlace", query = "SELECT e FROM Event e WHERE e.location = :location"),
        @NamedQuery(name = "Event.findByDateInterval", query = "SELECT e FROM Event e WHERE e.end >= :minStart AND e.end <= :maxEnd AND e.location.floor.building.id = :id"),
        @NamedQuery(name = "Event.findByBuilding", query = "SELECT e FROM Event e WHERE e.location.floor.building.id = :id")
})
@SequenceGenerator(name="gen_event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_event")
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date")
    private ZonedDateTime start;

    @Column(name = "end_date")
    private ZonedDateTime end;

    @ManyToOne
    @JoinColumn(name = "place_id", referencedColumnName = "id", nullable = false)
    private VisiblePOI location;

    public Event(){}

    public Event(long id, String title, String description, ZonedDateTime start, ZonedDateTime end, VisiblePOI location) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.start = start;
        this.end = end;
        this.location = location;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

    public VisiblePOI getLocation() {
        return location;
    }

    public void setLocation(VisiblePOI location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id == event.id &&
                Objects.equals(title, event.title) &&
                Objects.equals(description, event.description) &&
                start.isEqual(event.start) &&
                end.isEqual(event.end) &&
                Objects.equals(location, event.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, start, end, location);
    }

    public boolean matches(String request){
        return (this.title != null && StringUtils.unaccent(this.title).contains(request))
                || (this.description != null && StringUtils.unaccent(this.description).contains(request))
                || this.location.matches(request);
    }
}
