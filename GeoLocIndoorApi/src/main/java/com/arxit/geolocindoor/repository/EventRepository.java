package com.arxit.geolocindoor.repository;

import com.arxit.geolocindoor.common.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Event findById(long id);
    List<Event> findByBuilding(long id);
    List<Event> findByDateInterval(long id, ZonedDateTime minStart, ZonedDateTime maxEnd);
    void deleteById(long id);
}

