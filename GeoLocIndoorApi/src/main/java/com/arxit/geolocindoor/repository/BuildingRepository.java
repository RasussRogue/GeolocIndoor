package com.arxit.geolocindoor.repository;

import com.arxit.geolocindoor.common.entities.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    Building findById(long id);
    List<Building> findAll();
    void deleteById(long id);
}
