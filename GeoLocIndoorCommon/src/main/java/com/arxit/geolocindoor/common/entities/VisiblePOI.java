package com.arxit.geolocindoor.common.entities;

import com.arxit.geolocindoor.common.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.locationtech.jts.geom.Point;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@DiscriminatorValue("visible")
public class VisiblePOI extends POI {

    @Column(name = "title")
    private String title;

    @Column(name = "type")
    private String type;

    @ElementCollection
    private List<Event> events = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public VisiblePOI() {
        super();
    }

    public VisiblePOI(String title, String type) {
        this();
        this.title = title;
        this.type = type;
    }

    public VisiblePOI(String title, String type, Floor floor, Point geom) {
        super(floor, geom);
        this.title = title;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        VisiblePOI that = (VisiblePOI) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(type, that.type) &&
                super.equals(that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, type);
    }

    public boolean matches(String request){
        return (this.title != null && StringUtils.unaccent(this.title).contains(request))
                || (this.type != null && StringUtils.unaccent(this.type).contains(request));
    }

    @JsonIgnore
    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }

    @Override
    public String toString() {
        return "VisiblePOI{" +
                "title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", events=" + events +
                '}';
    }
}
