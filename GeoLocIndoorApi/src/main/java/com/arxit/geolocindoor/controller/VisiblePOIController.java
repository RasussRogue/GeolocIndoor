package com.arxit.geolocindoor.controller;

import com.arxit.geolocindoor.common.entities.*;
import com.arxit.geolocindoor.repository.FloorRepository;
import com.arxit.geolocindoor.service.BuildingService;
import com.arxit.geolocindoor.service.VisiblePOIService;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(value = "${server.api.path}")
public class VisiblePOIController {

    @Autowired
    private VisiblePOIService poiService;

    @GetMapping("/visibles/{floorId}")
    public List<VisiblePOI> getAllVisibles(@PathVariable("floorId") long id) {
        return poiService.getVisibles(id);
    }

    @PostMapping(value = "/visible/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public VisiblePOI addEvent(@RequestBody VisiblePOI poi) throws ParseException {
        return poiService.postVisible(poi);
    }

    @GetMapping("/types")
    public Set<String> getAllTypes() {
        return poiService.getTypes();
    }
}
