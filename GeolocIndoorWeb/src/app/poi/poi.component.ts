import * as mapboxgl from 'mapbox-gl';
import {Component, Input, OnInit} from '@angular/core';
import {BuildingModel} from '../model/building.model';
import {ApiService} from '../service/api.service';
import {PoiModel} from '../model/poi.model';
import * as WKT from 'terraformer-wkt-parser';
import {MatSnackBar} from '@angular/material/snack-bar';
import {FloorModel} from '../model/floor.model';

@Component({
  selector: 'gi-poi',
  templateUrl: 'poi.component.html'
})
export class PoiComponent implements OnInit {

  vInputBuilding: BuildingModel
  @Input()
  set inputBuilding(val) {
    this.vInputBuilding = val;
    this.ngOnInit();
  }

  newPOI = new PoiModel();

  latitude: number;
  longitude: number;

  types: string[];

  pickable = false;

  linkedPOI = [];

  constructor(private apiService: ApiService, private snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
    this.apiService.getPOITypes().subscribe(response => {
      this.types = response;
    });
    this.newPOI.floor = this.vInputBuilding.floors[0];
  }

  selectLocation(event: mapboxgl.LngLat) {
    this.longitude = event.lng;
    this.latitude = event.lat;
    this.newPOI.wkt = WKT.convert({type: 'Point', coordinates: [this.longitude, this.latitude]});
  }

  create() {
    console.log(this.newPOI);
    this.apiService.createPOI(this.newPOI).subscribe(response => {
      console.log(response);
      this.vInputBuilding.floors[this.vInputBuilding.floors.indexOf(this.newPOI.floor)].pois.push(response);
      const linkList = [];
      this.linkedPOI.forEach(l => {
        linkList.push({startId: l.id, endId: response.id});
      });
      console.log(linkList);
      this.apiService.addEdges(linkList).subscribe(res => {
        console.log(res);
      });
      this.snackBar.open('POI saved !', '', {
        duration: 2000,
      });
    });
  }

  refreshWKT() {
    this.newPOI.wkt = WKT.convert({type: 'Point', coordinates: [this.longitude, this.latitude]});
  }

  clear() {
    this.newPOI = new PoiModel();
  }

  setPickable() {
    this.pickable = !this.pickable;
  }

  addClickedPOI(poi) {
    this.linkedPOI.push(poi);
  }

  remove(poi) {
    this.linkedPOI.splice(this.linkedPOI.indexOf(poi), 1);
  }
}
