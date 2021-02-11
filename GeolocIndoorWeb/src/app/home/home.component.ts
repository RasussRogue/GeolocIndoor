import {Component, OnInit} from '@angular/core';
import {BuildingModel} from '../model/building.model';
import {EventModel} from '../model/event.model';

@Component({
  selector: 'gi-home',
  templateUrl: 'home.component.html',
})
export class HomeComponent implements OnInit {

  currentBuilding: BuildingModel = null;
  action = '';
  editableEvent: EventModel = null;

  ngOnInit(): void {

  }

  setCurrentBuilding(newBuilding, newAction) {
    this.currentBuilding = newBuilding;
    this.action = newAction;
  }

  setEditableButton(event) {
    this.action = 'edit';
    this.editableEvent = event;
  }
}
