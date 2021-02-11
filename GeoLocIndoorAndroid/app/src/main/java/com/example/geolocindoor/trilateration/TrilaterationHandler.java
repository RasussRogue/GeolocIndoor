package com.example.geolocindoor.trilateration;

import com.arxit.geolocindoor.common.entities.Beacon;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.RemoteBluetoothDevice;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.CoordinateTransformFactory;
import org.osgeo.proj4j.ProjCoordinate;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class TrilaterationHandler {
    private Map<String, IBeaconDevice> knownBeacons;
    private Map<String, Beacon> buildingBeacons;
    private String csName1 = "EPSG:4326";
    //lambert 93 coordinate reference system
    private String csName2 = "EPSG:2154";
    private CoordinateTransformFactory ctFactory;
    private CRSFactory csFactory;

    private CoordinateReferenceSystem crs1;
    private CoordinateReferenceSystem crs2;
    private CoordinateTransform transResult;

    private CoordinateTransform transBeacons;
    private ProjCoordinate p1;
    private ProjCoordinate p2;
    private ProjCoordinate pSource;
    private ProjCoordinate pDest;
    private double[][] positions;
    private double[] distances;
    private double[] solverResult;
    private double[] pointResult;

    private static double USER_HANDLE = 1.30;

    private static int NUMBER_OF_BEACONS = 3;
    private TrilaterationHandler(int n) {
        p1 = new ProjCoordinate();
        p2 = new ProjCoordinate();
        pSource = new ProjCoordinate();
        pDest = new ProjCoordinate();
        ctFactory = new CoordinateTransformFactory();
        csFactory = new CRSFactory();

        crs1 = csFactory.createFromName(csName1);
        crs2 = csFactory.createFromName(csName2);

        transBeacons = ctFactory.createTransform(crs1, crs2);
        transResult = ctFactory.createTransform(crs2, crs1);

        p1 = new ProjCoordinate();
        p2 = new ProjCoordinate();
        pSource = new ProjCoordinate();
        pDest = new ProjCoordinate();
        positions = new double[n][2];
        distances = new double[n];
        pointResult = new double[2];

    }

    public static TrilaterationHandler create(){
        return new TrilaterationHandler(NUMBER_OF_BEACONS);
    }

    public double[] solve(Map<String, IBeaconDevice> knownBeacons, Map<String, Beacon> buildingBeacons) {
        this.knownBeacons = knownBeacons;
        this.buildingBeacons = buildingBeacons;

        // trilateration  limited on 3 beacons
        List<RemoteBluetoothDevice> list = this.knownBeacons.values().stream().sorted(Comparator.comparing(RemoteBluetoothDevice::getDistance)).limit(this.positions.length).collect(Collectors.toList());

        for (int i = 0; i < positions.length; i++) {

            RemoteBluetoothDevice beacon = list.get(i);
            Beacon b = this.buildingBeacons.get(beacon.getUniqueId());
            if(b == null){
                continue;
            }
            p1.x = this.buildingBeacons.get(beacon.getUniqueId()).getLongitude();
            p1.y = this.buildingBeacons.get(beacon.getUniqueId()).getLatitude();
            transBeacons.transform(p1, p2);
            positions[i][0] = p2.x;
            positions[i][1] = p2.y;
            distances[i] = dist2D(beacon.getDistance(),b.getHeight()- USER_HANDLE);
        }

        if (distances[0] < 1) {
            distances[0] = distances[0] / 2;
        }

        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
        LeastSquaresOptimizer.Optimum optimum = solver.solve();

        solverResult = optimum.getPoint().toArray();

        pSource.x = solverResult[0];
        pSource.y = solverResult[1];
        transResult.transform(pSource, pDest);
        pointResult[0] = pDest.x;
        pointResult[1] = pDest.y;
        return pointResult;
    }

    private static double dist2D(double dist, double height){
        if(dist <= height){
            return Math.sqrt(Math.pow(height,2)-Math.pow(dist,2));
        }else{
            return Math.sqrt(Math.pow(dist,2)-Math.pow(height,2));
        }

    }
}
