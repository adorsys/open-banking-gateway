import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

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
      ibanDebitor: ['', Validators.required],
      ibanCreditor: ['', Validators.required],
      amount: ['', [Validators.required, Validators.min(0)]],
      purpose: ['']
    });
  }

  onConfirm() {
    console.log(this.paymentForm.get('pin').value);
  }

  onDeny() {}
}
