import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {BuildingModel} from '../model/building.model';
import {EventModel} from '../model/event.model';
import {FloorModel} from '../model/floor.model';
import {PoiModel} from '../model/poi.model';

@Injectable()
export class ApiService {

  constructor(private httpClient: HttpClient) {
  }

  getBuildings(): Observable<BuildingModel[]> {
    return this.httpClient.get('/api/buildings') as Observable<BuildingModel[]>;
  }

  createEvent(event: EventModel): Observable<EventModel> {
    return this.httpClient.post('/api/event/create', event) as Observable<EventModel>;
  }

  deleteEvent(event: EventModel): Observable<EventModel> {
    return this.httpClient.delete('/api/event/delete/' + event.id) as Observable<EventModel>;
  }

  getEventsByBuildingId(buildingModel: BuildingModel): Observable<EventModel[]> {
    return this.httpClient.get('/api/events/' + buildingModel.id) as Observable<EventModel[]>;
  }

  getVisiblePOIsByFloor(floorModel: FloorModel): Observable<PoiModel[]> {
    return this.httpClient.get('/api/visibles/' + floorModel.id) as Observable<PoiModel[]>;
  }

  getPOITypes(): Observable<string[]> {
    return this.httpClient.get('/api/types') as Observable<string[]>;
  }

  createPOI(poi: PoiModel): Observable<PoiModel> {
    return this.httpClient.post('/api/visible/create', poi) as Observable<PoiModel>;
  }

  addEdges(list: any): Observable<any> {
    return this.httpClient.post('/api/edge/create', list) as Observable<any>;
  }

  importBuilding(formData: FormData): Observable<any> {
    return this.httpClient.post<any>('/api/building/import', formData) as Observable<any>;
  }
}
