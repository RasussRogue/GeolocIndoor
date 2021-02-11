import {Component, Input, OnInit} from '@angular/core';
import {BuildingModel} from '../../model/building.model';
import {EventModel} from '../../model/event.model';
import {ApiService} from '../../service/api.service';

@Component({
  selector: 'gi-create-event',
  templateUrl: 'create-event.component.html'
})
export class CreateEventComponent implements OnInit {

  @Input() inputBuilding: BuildingModel;

  event: EventModel = new EventModel();

  constructor() {
  }

  ngOnInit() {

  }
}
