import {Component, Input} from '@angular/core';
import {PoiModel} from '../../model/poi.model';

@Component({
  selector: 'gi-poi-list',
  templateUrl: 'poi-list.component.html'
})
export class PoiListComponent {

  @Input() build: PoiModel[] = [];

  constructor() {
  }

}
