import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {RouterModule} from '@angular/router';
import {HeaderComponent} from './header/header.component';
import {MatToolbarModule} from '@angular/material/toolbar';
import {HomeComponent} from './home/home.component';
import {AppRoutingModule} from './app-routing.module';
import {BuildingListComponent} from './building/building-list/building-list.component';
import {ApiService} from './service/api.service';
import {HttpClientModule} from '@angular/common/http';
import { FlexLayoutModule } from '@angular/flex-layout';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatCardModule} from '@angular/material/card';
import {MatListModule} from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {BuildingDetailComponent} from './building/building-detail/building-detail.component';
import {MatTooltipModule} from '@angular/material/tooltip';
import {ConfirmtionDialogComponent, EventListComponent} from './event/event-list/event-list.component';
import {CreateEventComponent} from './event/create-event/create-event.component';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatNativeDateModule} from '@angular/material/core';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatDialogModule} from '@angular/material/dialog';
import {EditEventComponent} from './event/edit-event/edit-event.component';
import {FormEventComponent} from './event/form-event/form-event.component';
import {MapComponent} from './map/map.component';
import {PoiComponent} from './poi/poi.component';
import {PoiListComponent} from './poi/poi-list/poi-list.component';
import {CreateBuildingComponent} from './building/create-building/create-building.component';
import {MatSelectModule} from '@angular/material/select';
import {MatChipsModule} from '@angular/material/chips';


@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    HomeComponent,
    BuildingListComponent,
    BuildingDetailComponent,
    EventListComponent,
    CreateEventComponent,
    ConfirmtionDialogComponent,
    EditEventComponent,
    FormEventComponent,
    MapComponent,
    PoiComponent,
    PoiListComponent,
    CreateBuildingComponent
  ],
    imports: [
        HttpClientModule,
        BrowserModule,
        BrowserAnimationsModule,
        RouterModule,
        MatToolbarModule,
        AppRoutingModule,
        FlexLayoutModule,
        MatExpansionModule,
        MatFormFieldModule,
        MatInputModule,
        MatCardModule,
        MatListModule,
        MatIconModule,
        MatButtonModule,
        MatTooltipModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatAutocompleteModule,
        ReactiveFormsModule,
        FormsModule,
        MatDialogModule,
        MatSelectModule,
        MatChipsModule
    ],
  providers: [
    ApiService,
    MatDatepickerModule,
    MatSnackBar
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
