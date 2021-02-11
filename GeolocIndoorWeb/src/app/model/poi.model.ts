import {FloorModel} from './floor.model';
import {EventModel} from './event.model';

export class PoiModel {
   id: number;
   class = 'com.arxit.geolocindoor.common.entities.VisiblePOI';
   floor: FloorModel;
   title: string;
   type: string;
   events: EventModel[];
   wkt: string;
}
