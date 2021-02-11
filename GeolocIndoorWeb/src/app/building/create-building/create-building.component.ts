import {Component, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {ApiService} from '../../service/api.service';

@Component({
  selector: 'gi-create-building',
  templateUrl: 'create-building.component.html',
  styleUrls: ['create-building.component.css']
})
export class CreateBuildingComponent implements OnInit {

  @ViewChild('fileInput')
  fileInput;

  fileName = '';
  file: File | null = null;
  inputName = '';

  uploadForm: FormGroup;

  constructor(private formBuilder: FormBuilder, private apiService: ApiService) {
  }

  ngOnInit(): void {
    this.uploadForm = this.formBuilder.group({
      profile: ['']
    });
  }

  onClickFileInputButton(): void {
    this.fileInput.nativeElement.click();
  }

  onChangeFileInput(value): void {
    console.log(value);
    if (value.target.files.length > 0) {
      const file = value.target.files[0];
      this.fileName = file.name;
      this.uploadForm.get('profile').setValue(file);
    }
    console.log(value.target.files[0]);
  }

  postFile() {
    const formData = new FormData();
    formData.append('spatialiteFile', this.uploadForm.get('profile').value);
    formData.append('buildingName', this.inputName);
    this.apiService.importBuilding(formData).subscribe(response => {
      console.log(response);
    });
  }
}
