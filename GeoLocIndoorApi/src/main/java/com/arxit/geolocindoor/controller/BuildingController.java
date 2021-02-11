package com.arxit.geolocindoor.controller;

import com.arxit.geolocindoor.common.entities.Building;
import com.arxit.geolocindoor.service.BuildingService;
import com.google.common.flogger.FluentLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.util.List;

@RestController
@RequestMapping(value = "${server.api.path}")
public class BuildingController {

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    @Autowired
    private BuildingService buildingService;

    @GetMapping("/buildings")
    public List<Building> getAllBuildings() {
        LOG.atInfo().log("/buildings");
        return buildingService.getBuildings();
    }

    @GetMapping("/building/{id}")
    public Building getBuildingById(@PathVariable("id") long id) {
        LOG.atInfo().log("/building/%s",id);
        return buildingService.getBuildingById(id);
    }

    @GetMapping("/checksum/{id}")
    public long getChecksumById(@PathVariable("id") long id) {
        LOG.atInfo().log("/checksum/%s",id);
        return buildingService.getChecksumById(id);
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        var multipart = new CommonsMultipartResolver();
        multipart.setMaxUploadSize(50 * 1024 * (long) 1024);
        return multipart;
    }

    @PostMapping(value = "/building/import", consumes = "multipart/form-data")
    public Building importBuilding(@RequestParam("buildingName") String buildingName, @RequestParam("spatialiteFile")MultipartFile spatialiteFile){
        return this.buildingService.addBuilding(buildingName, spatialiteFile);
    }

    @DeleteMapping(value = "/building/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteEvent(@PathVariable("id") long id) {
        buildingService.deleteBuilding(id);
    }

}
