import {Component, EventEmitter, Input, Output} from '@angular/core';
import {EventModel} from '../../model/event.model';
import {BuildingModel} from '../../model/building.model';

@Component({
  selector: 'gi-edit-event',
  templateUrl: 'edit-event.component.html'
})
export class EditEventComponent {

  @Input() event: EventModel = new EventModel();
  @Input() inputBuilding: BuildingModel;
  @Output() eventChange = new EventEmitter<EventModel>();

  constructor() {
  }
}
