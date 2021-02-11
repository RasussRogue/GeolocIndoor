package com.arxit.geolocindoor.service;

import com.arxit.geolocindoor.common.entities.GraphEdge;
import com.arxit.geolocindoor.repository.GraphEdgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GraphEdgeService {

    @Autowired
    private GraphEdgeRepository edgeRepo;

    public List<GraphEdge> createEdges(List<GraphEdge> edges){
        return this.edgeRepo.saveAll(edges);
    }

}