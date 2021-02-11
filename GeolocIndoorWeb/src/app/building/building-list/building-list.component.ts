import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {ApiService} from '../../service/api.service';
import {BuildingModel} from '../../model/building.model';

@Component({
  selector: 'gi-building-list',
  templateUrl: 'building-list.component.html',
})
export class BuildingListComponent implements OnInit {

  @Output() buildingChange: EventEmitter<{building: BuildingModel, action: string}> = new EventEmitter<{building: BuildingModel, action: string}>();

  buildings: BuildingModel[];

  constructor(private apiService: ApiService) {
  }

  ngOnInit(): void {
    this.apiService.getBuildings().subscribe(response => {
      this.buildings = response;
    });
  }

  selectBuilding(building: BuildingModel, action: string) {
    this.buildingChange.emit({building, action});
  }

  deleteBuilding() {

  }
}
