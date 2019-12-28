import { Component, Input, OnInit }  from '@angular/core';
import { FormGroup } from '@angular/forms';
import { DynamicFormControlBase } from './dynamic.form.control.base';
import {DynamicFormFactory} from "./dynamic.form.factory";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'dynamic-form',
  templateUrl: './dynamic.form.component.html'
})
export class DynamicFormComponent implements OnInit {

  @Input() controlTemplates: DynamicFormControlBase<any>[] = [];
  @Input() submissionUri: string;
  form: FormGroup;

  constructor(private formFactory: DynamicFormFactory, private http: HttpClient) {  }

  ngOnInit() {
    this.form = this.formFactory.toFormGroup(this.controlTemplates);
  }
}
