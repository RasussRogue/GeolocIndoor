package com.arxit.geolocindoor.service;

import com.arxit.geolocindoor.common.entities.Building;
import com.arxit.geolocindoor.common.utils.ChecksumHandler;
import com.arxit.geolocindoor.parsing.SpatialiteParser;
import com.arxit.geolocindoor.repository.BuildingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Service
public class BuildingService {

    @Autowired
    private BuildingRepository buildRepo;

    public List<Building> getBuildings() {
        return buildRepo.findAll();
    }

    public Building getBuildingById(long id) {
        return buildRepo.findById(id);
    }

    public void deleteBuilding(long id) {
        buildRepo.deleteById(id);
    }

    public long getChecksumById(long id) {
        return new ChecksumHandler().computeBuildingChecksum(getBuildingById(id));
    }

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public Building addBuilding(String name, MultipartFile spatialiteFile){
        var parser = new SpatialiteParser();
        try {
            var file = File.createTempFile("building-import", ".sqlite");
            var path = Paths.get(file.getAbsolutePath());
            Files.write(path, spatialiteFile.getBytes(), StandardOpenOption.CREATE);
            var building = parser.getBuilding(path, name);
            this.persistBuilding(building);
            Files.delete(path);
            return building;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void persistBuilding(Building building){
        this.entityManager.persist(building);
        building.getFloors().forEach(floor -> {
            this.entityManager.persist(floor);
            floor.getBounds().forEach(this.entityManager::persist);
            floor.getWalls().forEach(this.entityManager::persist);
            floor.getBeacons().forEach(this.entityManager::persist);
            floor.getPois().forEach(this.entityManager::persist);
        });
        building.getEdges().forEach(this.entityManager::persist);
    }
}
