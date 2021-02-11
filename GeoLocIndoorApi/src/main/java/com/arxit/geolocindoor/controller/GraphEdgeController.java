package com.arxit.geolocindoor.controller;

import com.arxit.geolocindoor.common.entities.GraphEdge;
import com.arxit.geolocindoor.dpo.GraphEdgeDpo;
import com.arxit.geolocindoor.repository.POIRepository;
import com.arxit.geolocindoor.service.GraphEdgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "${server.api.path}")
public class GraphEdgeController {

    @Autowired
    private GraphEdgeService edgeService;

    @Autowired
    private POIRepository poiRepo;

    @PostMapping("edge/create")
    public List<GraphEdge> createEdges(@RequestBody List<GraphEdgeDpo> dpos){

        return this.edgeService.createEdges(
                dpos.stream().map(dpo -> {
                    var startOpt = this.poiRepo.findById(dpo.getStartId());
                    var endOpt = this.poiRepo.findById(dpo.getEndId());
                    if (startOpt.isEmpty() || endOpt.isEmpty()){
                        throw new RuntimeException("POI NOT FOUND");
                    }
                    var start = startOpt.get();
                    var end = endOpt.get();
                    var value = start.getFloor().getLevel() != end.getFloor().getLevel() ? 4 :
                            distance(start.getLatitude(), end.getLatitude(), start.getLongitude(), end.getLongitude());
                    return new GraphEdge(start, end, value, start.getFloor().getBuilding());
                }).collect(Collectors.toList())
        );
    }

    /**
     * Computes distance in meters between two WGS84 coordinates
     * @param lat1 first point's latitude
     * @param lat2 second point's longitude
     * @param lon1 first point's latitude
     * @param lon2 second point's longitude
     * @return distance in meters
     */
    private static double distance(double lat1, double lat2, double lon1, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000;
    }
}
