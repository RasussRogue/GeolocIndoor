package com.arxit.geolocindoor.repository;


import com.arxit.geolocindoor.common.entities.Building;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BuildingRepositoryTest {

    @Autowired
    private BuildingRepository buildingRepository;

    @Test
    public void whenFindById_thenReturnCorrectBuilding() {
        // given
        Building copernic = new Building();
        copernic.setName("Copernic");
        copernic.setFloors(new ArrayList<>());
        copernic.setEdges(new ArrayList<>());
        buildingRepository.save(copernic);

        // when
        Building found = buildingRepository.findById(copernic.getId());

        // then
        assertThat(found.getName())
                .isEqualTo(copernic.getName());
    }

    @Test
    public void whenFindAll_thenReturnAllBuildings() {
        // given
        Building copernic = new Building();
        copernic.setName("Copernic");
        //copernic.setId(0);
        Building lavoisier = new Building();
        lavoisier.setName("Lavoisier");
        //lavoisier.setId(1);

        buildingRepository.save(copernic);
        buildingRepository.save(lavoisier);

        // when
        List<Building> found = buildingRepository.findAll();

        // then
        assertThat(found)
                .contains(copernic)
                .contains(lavoisier);


    }
}
