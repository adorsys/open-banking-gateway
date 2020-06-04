import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FintechSinglePaymentInitiationService } from '../../api/api/fintechSinglePaymentInitiation.service';
import { ClassSinglePaymentInitiationRequest } from '../../api/model-classes/ClassSinglePaymentInitiationRequest';
import { ActivatedRoute, Router } from '@angular/router';


@Component({
  selector: 'app-initiate',
  templateUrl: './initiate.component.html',
  styleUrls: ['./initiate.component.scss']
})
export class InitiateComponent implements OnInit {
  public static ROUTE = 'initiate';
  bankId = null;

  paymentForm: FormGroup;

  constructor(private formBuilder: FormBuilder,
              private fintechSinglePaymentInitiationService: FintechSinglePaymentInitiationService,
              private router: Router,
              private route: ActivatedRoute
  ) {
    this.bankId = this.route.snapshot.paramMap.get('bankid');
    console.log('bankid:' + this.bankId);
  }

  ngOnInit() {
    this.paymentForm = this.formBuilder.group({
      name: ['peter', Validators.required],
      ibanDebitor: ['DE38', Validators.required],
      ibanCreditor: ['DE39', Validators.required],
      amount: ['12.34', [Validators.required, Validators.min(0)]],
      purpose: ['money test']
    });
  }

  onConfirm() {
    const paymentRequest = new ClassSinglePaymentInitiationRequest();
    paymentRequest.amount = this.paymentForm.getRawValue().amount;
    paymentRequest.name = this.paymentForm.getRawValue().name;
    paymentRequest.creditorIban = this.paymentForm.getRawValue().ibanCreditor;
    paymentRequest.debitorIban = this.paymentForm.getRawValue().ibanDebitor;
    paymentRequest.purpose = this.paymentForm.getRawValue().purpose;
    this.fintechSinglePaymentInitiationService.initiateSinglePayment('', '', '',
      '', this.bankId, paymentRequest).subscribe(() => console.log('call was done'));
  }

  onDeny() {
  }
}
