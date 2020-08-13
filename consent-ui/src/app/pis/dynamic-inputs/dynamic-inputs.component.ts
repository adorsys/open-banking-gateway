import { Component, Input, OnInit } from '@angular/core';
import { AuthViolation } from '../../api';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { PisPayment } from '../common/models/pis-payment.model';

@Component({
  selector: 'consent-app-dynamic-inputs',
  templateUrl: './dynamic-inputs.component.html',
  styleUrls: ['./dynamic-inputs.component.scss']
})
export class DynamicInputsComponent implements OnInit {
  @Input() violations: AuthViolation[];
  @Input() targetForm: FormGroup;
  @Input() payment: PisPayment;

  constructor() {}

  ngOnInit() {
    this.violations.forEach(it => this.targetForm.addControl(it.code, new FormControl('', Validators.required)));

    if (this.payment && this.payment.extras) {
      this.violations
        .filter(it => this.targetForm.get(it.code) && this.payment.extras[it.code])
        .forEach(it => this.targetForm.get(it.code).setValue(this.payment.extras[it.code]));
    }
  }
}
