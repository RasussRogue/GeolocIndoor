import {PoiModel} from './poi.model';

export class EventModel {
  id: number;
  title: string;
  description: string;
  start: Date = new Date(0);
  end: Date = new Date(0);
  location: PoiModel = new PoiModel();
}
