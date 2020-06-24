import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ValidatorService } from 'angular-iban';
import { FintechSinglePaymentInitiationService } from '../../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { ClassSinglePaymentInitiationRequest } from '../../../api/model-classes/ClassSinglePaymentInitiationRequest';
import { map } from 'rxjs/operators';
import { HeaderConfig } from '../../../models/consts';
import { RedirectStruct, RedirectType } from '../../redirect-page/redirect-struct';
import { StorageService } from '../../../services/storage.service';
import { ConfirmData } from '../payment-confirm/confirm.data';

@Component({
  selector: 'app-initiate',
  templateUrl: './initiate.component.html',
  styleUrls: ['./initiate.component.scss']
})
export class InitiateComponent implements OnInit {
  public static ROUTE = 'initiate';
  bankId = '';
  accountId = '';

  paymentForm: FormGroup;
  constructor(private formBuilder: FormBuilder,
              private fintechSinglePaymentInitiationService: FintechSinglePaymentInitiationService,
              private router: Router,
              private route: ActivatedRoute,
              private storageService: StorageService) {
    this.bankId = this.route.snapshot.paramMap.get('bankid');
    this.accountId = this.route.snapshot.paramMap.get('accountid');
    console.log('bankid:', this.bankId, ' accountid:', this.accountId);
  }

  ngOnInit() {
    this.paymentForm = this.formBuilder.group({
      name: ['peter', Validators.required],
      creditorIban: ['AL90208110080000001039531801', [ValidatorService.validateIban, Validators.required]],
      debitorIban: ['DE80760700240271232400', [ValidatorService.validateIban, Validators.required]],
      amount: ['12.34', [Validators.pattern('^[1-9]\\d*(\\.\\d{1,2})?$'), Validators.required]],
      purpose: ['test transfer']
    });
  }

  onConfirm() {
    // TODO
    let okurl = window.location.pathname;
    okurl = okurl.replace('/initiate', '/loa');
    const notOkUrl = okurl;
    console.log('set ok url to ', okurl);

    const paymentRequest = new ClassSinglePaymentInitiationRequest();
    paymentRequest.amount = this.paymentForm.getRawValue().amount;
    paymentRequest.name = this.paymentForm.getRawValue().name;
    paymentRequest.creditorIban = this.paymentForm.getRawValue().creditorIban;
    paymentRequest.debitorIban = this.paymentForm.getRawValue().debitorIban;
    paymentRequest.purpose = this.paymentForm.getRawValue().purpose;
    this.fintechSinglePaymentInitiationService.initiateSinglePayment(this.bankId, this.accountId,'',
      '',okurl, notOkUrl, paymentRequest, 'response')
      .pipe(map(response => response))
      .subscribe(
        response => {
          console.log('response status of payment call is ', response.status)
          switch (response.status) {
            case 202:
              const location = response.headers.get(HeaderConfig.HEADER_FIELD_LOCATION);
              this.storageService.setRedirect(
                response.headers.get(HeaderConfig.HEADER_FIELD_REDIRECT_CODE),
                response.headers.get(HeaderConfig.HEADER_FIELD_AUTH_ID),
                response.headers.get(HeaderConfig.HEADER_FIELD_X_XSRF_TOKEN),
                parseInt(response.headers.get(HeaderConfig.HEADER_FIELD_REDIRECT_X_MAX_AGE), 0),
                RedirectType.PIS
              );

              const confirmData = new ConfirmData();
              confirmData.paymentRequest = paymentRequest;
              confirmData.redirectStruct = new RedirectStruct();
              confirmData.redirectStruct.redirectUrl = encodeURIComponent(location);
              confirmData.redirectStruct.redirectCode = response.headers.get(HeaderConfig.HEADER_FIELD_REDIRECT_CODE);
              confirmData.redirectStruct.bankId = this.bankId;
              confirmData.redirectStruct.bankName = this.storageService.getBankName();

              this.router.navigate(['../confirm', JSON.stringify(confirmData)], { relativeTo: this.route });
              break;
          }
        });
  }

  onDeny() {
    this.router.navigate(['../../'], { relativeTo: this.route });
  }

  get debitorIban() {
    return this.paymentForm.get('debitorIban');
  }

  get creditorIban() {
    return this.paymentForm.get('creditorIban');
  }
}
