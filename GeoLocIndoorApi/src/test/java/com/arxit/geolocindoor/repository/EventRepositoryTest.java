package com.arxit.geolocindoor.repository;

import com.arxit.geolocindoor.common.entities.Event;
import com.arxit.geolocindoor.common.entities.VisiblePOI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void whenFindById_thenReturnCorrectEvent() {
        // given
        Event event = new Event();
        event.setTitle("Barmitzva");
        eventRepository.save(event);

        // when
        Event found = eventRepository.findById(event.getId());

        // then
        assertThat(found.getTitle())
                .isEqualTo(event.getTitle());
    }

    @Test
    public void whenDeleteEvent_thenCorrectlyRemovedFromBase() {
        // given
        Event event = new Event();
        event.setTitle("Barmitzva");
        eventRepository.save(event);

        // when
        eventRepository.deleteById(event.getId());

        // then
        assertNull(eventRepository.findById(event.getId()));
    }
}
