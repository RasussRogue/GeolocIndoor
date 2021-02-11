package com.arxit.geolocindoor.repository;

import com.arxit.geolocindoor.common.entities.Building;
import com.arxit.geolocindoor.common.entities.Floor;
import com.arxit.geolocindoor.common.entities.VisiblePOI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisiblePOIRepository extends JpaRepository<VisiblePOI, Long> {
    List<VisiblePOI> findAllByFloorId(long id);
    VisiblePOI findById(long id);
}
