import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FintechSinglePaymentInitiationService } from '../../api/api/fintechSinglePaymentInitiation.service';
import { ClassSinglePaymentInitiationRequest } from '../../api/model-classes/ClassSinglePaymentInitiationRequest';
import { ActivatedRoute, Router } from '@angular/router';
import { HeaderConfig } from '../../models/consts';
import { RedirectStruct } from '../../bank/redirect-page/redirect-struct';
import { map } from 'rxjs/operators';


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
      ibanDebitor: ['DE80760700240271232400', Validators.required],
      ibanCreditor: ['DE80760700240271232400', Validators.required],
      amount: ['12.34', [Validators.required, Validators.min(0)]],
      purpose: ['money test']
    });
  }

  onConfirm() {
    // TODO
    const okurl = window.location.pathname;
    const notOkUrl = okurl;
    console.log('WARNING set ok url to {}', okurl);

    const paymentRequest = new ClassSinglePaymentInitiationRequest();
    paymentRequest.amount = this.paymentForm.getRawValue().amount;
    paymentRequest.name = this.paymentForm.getRawValue().name;
    paymentRequest.creditorIban = this.paymentForm.getRawValue().ibanCreditor;
    paymentRequest.debitorIban = this.paymentForm.getRawValue().ibanDebitor;
    paymentRequest.purpose = this.paymentForm.getRawValue().purpose;
    this.fintechSinglePaymentInitiationService.initiateSinglePayment('', '',
      okurl, notOkUrl, this.bankId, paymentRequest, 'response')
      .pipe(map(response => response))
      .subscribe(
        response => {
          console.log('response status of payment call is ', response.status)
          switch (response.status) {
            case 202:
              const location = response.headers.get(HeaderConfig.HEADER_FIELD_LOCATION);
              console.log('location is ', location);
              window.location.href = location;
          }
        });
  }

  onDeny() {
  }
}
