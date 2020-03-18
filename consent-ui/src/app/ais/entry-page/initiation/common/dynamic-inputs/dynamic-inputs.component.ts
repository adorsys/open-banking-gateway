import { Component, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthViolation } from '../../../../../api';

@Component({
  selector: 'consent-app-dynamic-inputs',
  templateUrl: './dynamic-inputs.component.html',
  styleUrls: ['./dynamic-inputs.component.scss']
})
export class DynamicInputsComponent implements OnInit {
  @Input() violations: AuthViolation[];
  @Input() targetForm: FormGroup;

  constructor() {}

  ngOnInit() {
    this.violations.forEach(it => this.targetForm.addControl(it.code, new FormControl('', Validators.required)));
  }
}
