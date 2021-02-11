import {Component, Input, OnInit, Output} from '@angular/core';
import * as mapboxgl from 'mapbox-gl';
import {environment} from '../../environments/environment';
import {EventEmitter} from '@angular/core';
import {FloorModel} from '../model/floor.model';
import * as WKT from 'terraformer-wkt-parser';

@Component({
    selector: 'gi-map',
    templateUrl: 'map.component.html',
    styleUrls: ['map.component.css']
})
export class MapComponent implements OnInit {

    @Output() clickOnMap = new EventEmitter<mapboxgl.LngLat>();
    vFloor: FloorModel;

    @Output() clickedPOI = new EventEmitter<{title: string, id: number}>();

    @Input()
    set floor(value: FloorModel) {
        this.vFloor = value;
        if (this.map) {
            this.map.getSource('walls').setData(this.getWallsData());
            this.map.getSource('bounds').setData(this.getBoundsData());
            this.map.getSource('visiblePOI').setData(this.getVisiblePOIData());
            this.map.getSource('pois').setData(this.getPOIData());
        }
    }

    @Input() pickable: boolean;
    @Output() pickableChange: EventEmitter<boolean> = new EventEmitter<boolean>();

    map: mapboxgl.Map;
    style = 'mapbox://styles/mapbox/streets-v11';
    marker: mapboxgl.Marker = null;
    pointer: mapboxgl.LngLat;
    lat = 48.839317;
    lng = 2.587141;

    constructor() {
    }

    ngOnInit(): void {
        Object.getOwnPropertyDescriptor(mapboxgl, 'accessToken').set(environment.mapbox.accessToken);
        this.map = new mapboxgl.Map({
            container: 'map',
            style: this.style,
            zoom: 17,
            center: [this.lng, this.lat],
            attributionControl: false,
        });

        const marker = new mapboxgl.Marker();

        this.map.on('click', response => {
            if (marker === null) {
                this.marker = new mapboxgl.Marker();
            }

            const bbox = [
                [response.point.x - 5, response.point.y - 5],
                [response.point.x + 5, response.point.y + 5]
            ];

            const features = this.map.queryRenderedFeatures(bbox, {
                layers: ['visiblePOI', 'pois']
            });

            if (features.length > 0) {
                const tempId: number = features[0].properties.id;
                const tempTitle: string = features[0].properties.title;
                this.clickedPOI.emit({title: tempTitle, id: tempId});
            }

            if (this.pickable) {
                this.clickOnMap.emit(response.lngLat);
                marker.setLngLat(response.lngLat);
                marker.addTo(this.map);
                this.pickableChange.emit(false);
            }
        });

        this.map.on('load', response => {
            this.map.addSource('walls', {
                type: 'geojson',
                data: this.getWallsData()
            });

            this.map.addSource('bounds', {
                type: 'geojson',
                data: this.getBoundsData()
            });

            this.map.addSource('visiblePOI', {
                type: 'geojson',
                data: this.getVisiblePOIData()
            });

            this.map.addSource('pois', {
                type: 'geojson',
                data: this.getPOIData()
            });

            this.map.addLayer({
                id: 'bounds',
                type: 'fill',
                source: 'bounds',
                paint: {
                    'fill-color': ['get', 'color']
                }
            });

            this.map.addLayer({
                id: 'walls',
                type: 'line',
                source: 'walls',
                paint: {
                    'line-width': 3,
                    'line-color': ['get', 'color']
                }
            });

            this.map.addLayer({
                id: 'visiblePOI',
                type: 'circle',
                source: 'visiblePOI',
                paint: {
                    'circle-radius': 7,
                    'circle-color': ['get', 'color']
                }
            });

            this.map.addLayer({
                id: 'pois',
                type: 'circle',
                source: 'pois',
                paint: {
                    'circle-radius': 5,
                    'circle-color': ['get', 'color']
                }
            });

        });
    }

    getWallsData() {
        const list = [];
        this.vFloor.walls.forEach(wall => {
            // @ts-ignore
            const coord = WKT.parse(wall.wkt).coordinates;
            list.push({
                type: 'Feature',
                properties: {
                    color: '#000000'
                },
                geometry: {
                    type: 'MultiLineString',
                    coordinates: coord
                }
            });
        });
        return {
            type: 'FeatureCollection',
            features: list
        };
    }

    getBoundsData() {
        const list = [];
        this.vFloor.bounds.forEach(bound => {
            // @ts-ignore
            const coord = WKT.parse(bound.wkt).coordinates;
            list.push({
                type: 'Feature',
                properties: {
                    color: '#DDD9D6'
                },
                geometry: {
                    type: 'MultiPolygon',
                    coordinates: coord
                }
            });
        });
        return {
            type: 'FeatureCollection',
            features: list
        };
    }

    getVisiblePOIData() {
        const list = [];
        this.vFloor.pois.forEach(poi => {
            if (!poi.type) {
                return;
            }
            // @ts-ignore
            const coord = WKT.parse(poi.wkt).coordinates;
            list.push({
                type: 'Feature',
                properties: {
                    color: '#3f51b5',
                    id: poi.id,
                    title: poi.title
                },
                geometry: {
                    type: 'Point',
                    coordinates: coord
                }
            });
        });
        return {
            type: 'FeatureCollection',
            features: list
        };
    }

    getPOIData() {
        const list = [];
        this.vFloor.pois.forEach(poi => {
            if (poi.type) {
                return;
            }
            // @ts-ignore
            const coord = WKT.parse(poi.wkt).coordinates;
            list.push({
                type: 'Feature',
                properties: {
                    color: '#990000',
                    id: poi.id
                },
                geometry: {
                    type: 'Point',
                    coordinates: coord
                }
            });
        });
        return {
            type: 'FeatureCollection',
            features: list
        };
    }
}
