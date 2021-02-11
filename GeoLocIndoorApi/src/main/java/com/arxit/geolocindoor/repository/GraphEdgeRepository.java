package com.arxit.geolocindoor.repository;

import com.arxit.geolocindoor.common.entities.GraphEdge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GraphEdgeRepository extends JpaRepository<GraphEdge, Long> {

}
