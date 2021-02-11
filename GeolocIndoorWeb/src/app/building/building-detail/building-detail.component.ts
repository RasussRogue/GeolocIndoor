import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {BuildingModel} from '../../model/building.model';
import {EventModel} from '../../model/event.model';
import {ApiService} from '../../service/api.service';

@Component({
  selector: 'gi-building-detail',
  templateUrl: 'building-detail.component.html'
})
export class BuildingDetailComponent implements OnInit {

  building: BuildingModel = null;
  events: EventModel[];

  @Input()
  set inputBuilding(val: BuildingModel) {
    this.building = val;
    this.apiService.getEventsByBuildingId(this.building).subscribe(response => {
      this.events = response;
      this.events = this.events.sort((event1, event2) => {
        if (event1.id > event2.id) {
          return 1;
        }
        if (event1.id < event2.id) {
          return -1;
        }
        return 0;
      });
      console.log(response);
    });
  }

  @Output() editableButton = new EventEmitter<EventModel>();

  constructor(private apiService: ApiService) {
  }

  ngOnInit(): void {

  }

  delete(event) {
    console.log(event);
    this.events.splice(this.events.indexOf(event), 1);
  }

  notifyEdition(event) {
    this.editableButton.emit(event);
  }
}
