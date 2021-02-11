import {Component, EventEmitter, Inject, Input, Output} from '@angular/core';
import {EventModel} from '../../model/event.model';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {ApiService} from '../../service/api.service';
import {BuildingModel} from '../../model/building.model';

@Component({
  selector: 'gi-event-list',
  templateUrl: 'event-list.component.html'
})
export class EventListComponent {

  @Input() building: BuildingModel;
  @Input() events: EventModel[];
  @Output() deleted: EventEmitter<EventModel> = new EventEmitter<EventModel>();
  @Output() editableButton: EventEmitter<EventModel> = new EventEmitter<EventModel>();

  constructor(public dialog: MatDialog, private apiService: ApiService) {
  }

  openDialog(event: EventModel) {
    const dialogRef = this.dialog.open(ConfirmtionDialogComponent, {
      data: event
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.deleted.emit(event);
      }
    });
  }

  notifyEdition(event) {
    this.editableButton.emit(event);
  }
}

@Component({
  selector: 'gi-confirmation-dialog',
  templateUrl: 'confirmation-dialog.html',
})
export class ConfirmtionDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ConfirmtionDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public event: EventModel,
    private apiService: ApiService) {}

  deleteEvent(): void {
    this.apiService.deleteEvent(this.event).subscribe(
      () => {
        this.dialogRef.close(true);
      }
    );
  }
}
