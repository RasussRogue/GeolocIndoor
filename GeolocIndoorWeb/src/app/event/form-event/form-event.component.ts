import {ChangeDetectorRef, Component, Input, OnInit, ViewChild} from '@angular/core';
import {EventModel} from '../../model/event.model';
import {MatOptionSelectionChange} from '@angular/material/core';
import {ApiService} from '../../service/api.service';
import {FormControl} from '@angular/forms';
import {MatDatepickerInputEvent} from '@angular/material/datepicker';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatInput} from '@angular/material/input';
import {map, startWith} from 'rxjs/operators';
import {BuildingModel} from '../../model/building.model';
import {Observable} from 'rxjs';
import {PoiModel} from '../../model/poi.model';

@Component({
  selector: 'gi-form-event',
  templateUrl: 'form-event.component.html'
})
export class FormEventComponent implements OnInit {

  event: EventModel = new EventModel();

  @Input() inputBuilding: BuildingModel;

  oldFields: EventModel = new EventModel();
  dateStart = new FormControl();
  dateEnd = new FormControl();
  poiControl = new FormControl();

  filteredPOIs: Observable<PoiModel[]>;

  @ViewChild('startTimeInput', {read: MatInput}) startTimeInput: MatInput;
  @ViewChild('endTimeInput', {read: MatInput}) endTimeInput: MatInput;
  @ViewChild('startDateInput', {read: MatInput}) startDateInput: MatInput;
  @ViewChild('endDateInput', {read: MatInput}) endDateInput: MatInput;

  pois: PoiModel[] = [];

  @Input()
  set setEvent(event: EventModel) {
    this.event = event;
    this.event.start = new Date(this.event.start);
    this.event.end = new Date(this.event.end);
    this.loadFields();
  }

  constructor(private apiService: ApiService, private snackBar: MatSnackBar, private chRef: ChangeDetectorRef) {
  }

  ngOnInit() {
    this.inputBuilding.floors.forEach(floor => {
      this.apiService.getVisiblePOIsByFloor(floor).subscribe(response => {
        this.pois = this.pois.concat(response);

        this.filteredPOIs = this.poiControl.valueChanges
          .pipe(
            startWith(''),
            map(value => this._filter(value))
          );
      });
    });
  }

  setLocation(ref: MatOptionSelectionChange) {
    this.event.location = ref.source.value;
    this.poiControl.setValue(ref.source.value.title);
    // this.poiControl.setValue(ref.source.value.title);
  }

  saveEvent() {
    this.apiService.createEvent(this.event).subscribe(response => {
      this.snackBar.open('Event saved !', '', {
        duration: 2000,
      });
    });
    this.clearFields();
  }


  setTime(moment: string, value: Date) {
    switch (moment) {
      case 'start':
        this.event.start.setHours(value.getHours());
        this.event.start.setMinutes(value.getMinutes());
        break;
      case 'end':
        this.event.end.setHours(value.getHours());
        this.event.end.setMinutes(value.getMinutes());
        break;
    }
  }

  clearFields() {
    this.oldFields = this.event;
    this.event = new EventModel();
    this.startTimeInput.value = '';
    this.endTimeInput.value = '';
    this.startDateInput.value = '';
    this.endDateInput.value = '';
    this.poiControl.setValue('');
  }

  clearFieldsWithNotification() {
    this.oldFields = this.event;
    this.event = new EventModel();
    this.startTimeInput.value = '';
    this.endTimeInput.value = '';
    this.startDateInput.value = '';
    this.endDateInput.value = '';
    this.poiControl.setValue('');
    const ref = this.snackBar.open('Fields cleared !', 'Cancel', {
      duration: 5000,
    });
    ref.afterDismissed().subscribe(response => {
      if (response.dismissedByAction === true) {
        this.refreshValues();
      }
    });
  }

  refreshValues() {
    this.event = this.oldFields;
    this.loadFields();
  }

  loadFields() {
    this.chRef.detectChanges();
    let hoursStart = '';
    let hoursEnd = '';
    let minutesStart = '';
    let minutesEnd = '';

    (this.event?.start.getHours() === 0) ? hoursStart = '23' : hoursStart = (this.event.start.getHours() - 1).toString();
    (this.event?.end.getHours() === 0) ? hoursEnd = '23' : hoursEnd = (this.event.end.getHours() - 1).toString();
    if (hoursStart === '0') {
      hoursStart = '00';
    }
    if (hoursEnd === '0') {
      hoursEnd = '00';
    }

    (this.event.start.getMinutes() === 0) ? minutesStart = '00' : minutesStart = this.event.start.getMinutes().toString();
    (this.event.end.getMinutes() === 0) ?  minutesEnd = '00' : minutesEnd = this.event.end.getMinutes().toString();

    this.startTimeInput.value = hoursStart + ':' + minutesStart;
    this.endTimeInput.value = hoursEnd + ':' + minutesEnd;

    if (this.event.start.getFullYear() > 2018) { this.dateStart = new FormControl(this.event.start); }
    if (this.event.end.getFullYear() > 2018) { this.dateEnd = new FormControl(this.event.end); }

    this.poiControl.setValue(this.event.location.title);
  }

  setDate(moment: string, value: MatDatepickerInputEvent<any>) {
    const newDate = new Date(value.value);
    switch (moment) {
      case 'start':
        this.event.start.setDate(newDate.getDate());
        this.event.start.setMonth(newDate.getMonth());
        this.event.start.setFullYear(newDate.getFullYear());
        break;
      case 'end':
        this.event.end.setDate(newDate.getDate());
        this.event.end.setMonth(newDate.getMonth());
        this.event.end.setFullYear(newDate.getFullYear());
        break;
    }
  }

  private _filter(value: string): PoiModel[] {
    if (value === null) {
      return;
    }
    const filterValue = value.toString().toLowerCase();
    return this.pois.filter(option => option.title.toLowerCase().includes(filterValue));
  }
}

