import {WallModel} from './wall.model';
import {BoundModel} from './bound.model';
import {PoiModel} from './poi.model';

export class FloorModel {
  id: number;
  name: string;
  level: number;
  walls: WallModel[];
  bounds: BoundModel[];
  pois: PoiModel[];
}
