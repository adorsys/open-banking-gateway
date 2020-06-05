import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ValidatorService } from 'angular-iban';

@Component({
  selector: 'app-initiate',
  templateUrl: './initiate.component.html',
  styleUrls: ['./initiate.component.scss']
})
export class InitiateComponent implements OnInit {
  public static ROUTE = 'initiate';

  paymentForm: FormGroup;
  constructor(private formBuilder: FormBuilder) {}

  ngOnInit() {
    this.paymentForm = this.formBuilder.group({
      name: ['', Validators.required],
      debitorIban: ['', [ValidatorService.validateIban, Validators.required]],
      creditorIban: ['', [ValidatorService.validateIban, Validators.required]],
      amount: ['', [Validators.pattern('^[1-9]\\d*(\\.\\d{1,2})?$'), Validators.required]],
      purpose: ['']
    });
  }

  onConfirm() {
    console.log(this.paymentForm.getRawValue());
  }

  onDeny() {}

  get debitorIban() {
    return this.paymentForm.get('debitorIban');
  }

  get creditorIban() {
    return this.paymentForm.get('creditorIban');
  }
}
