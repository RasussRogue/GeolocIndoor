package com.arxit.geolocindoor.service;

import com.arxit.geolocindoor.common.entities.Event;
import com.arxit.geolocindoor.common.entities.VisiblePOI;
import com.arxit.geolocindoor.repository.FloorRepository;
import com.arxit.geolocindoor.repository.VisiblePOIRepository;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class VisiblePOIService {
    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private VisiblePOIRepository poiRepo;

    public List<VisiblePOI> getVisibles(long id) {
        return poiRepo.findAllByFloorId(id);
    }

    public Set<String> getTypes() {
        List<VisiblePOI> pois = poiRepo.findAll();
        Set<String> ret = new HashSet<>();
        for (VisiblePOI v : pois) ret.add(v.getType());
        return ret;
    }

    public VisiblePOI postVisible(VisiblePOI poi) throws ParseException {
        VisiblePOI nPoi = new VisiblePOI();
        Point point = (Point) new WKTReader().read(poi.getWkt());
        point.setSRID(4326);
        nPoi.setGeom(point);
        nPoi.setWkt(poi.getWkt());
        nPoi.setTitle(poi.getTitle());
        nPoi.setType(poi.getType());
        floorRepository.findById(poi.getFloor().getId()).ifPresent(nPoi::setFloor);
        return poiRepo.save(poi);
    }
}
