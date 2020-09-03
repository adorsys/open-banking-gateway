import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ValidatorService } from 'angular-iban';
import { FintechSinglePaymentInitiationService, SinglePaymentInitiationRequest } from '../../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { HeaderConfig } from '../../../models/consts';
import { RedirectStruct, RedirectType } from '../../redirect-page/redirect-struct';
import { StorageService } from '../../../services/storage.service';
import { ConfirmData } from '../payment-confirm/confirm.data';
import { HttpResponse } from '@angular/common/http';

@Component({
  selector: 'app-initiate',
  templateUrl: './initiate.component.html',
  styleUrls: ['./initiate.component.scss']
})
export class InitiateComponent implements OnInit {
  public static ROUTE = 'initiate';
  bankId = '';
  accountId = '';
  debitorIban = '';
  paymentForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private fintechSinglePaymentInitiationService: FintechSinglePaymentInitiationService,
    private router: Router,
    private route: ActivatedRoute,
    private storageService: StorageService
  ) {
    this.bankId = this.route.snapshot.paramMap.get('bankid');
    this.accountId = this.route.snapshot.paramMap.get('accountid');
  }

  ngOnInit() {
    this.debitorIban = this.getDebitorIban(this.accountId);
    this.paymentForm = this.formBuilder.group({
      name: ['test user', Validators.required],
      creditorIban: ['AL90208110080000001039531801', [ValidatorService.validateIban, Validators.required]],
      amount: ['12.34', [Validators.pattern('^[1-9]\\d*(\\.\\d{1,2})?$'), Validators.required]],
      purpose: ['test transfer']
    });
  }

  onConfirm(): void {
    let okurl = window.location.pathname;
    const notOkUrl = okurl.replace('/payment/.*', '/payment/accounts');
    okurl = okurl.replace('/initiate', '/payments');
    console.log('set urls to ', okurl, '', notOkUrl);

    const paymentRequest: SinglePaymentInitiationRequest = { ...this.paymentForm.getRawValue() };
    paymentRequest.debitorIban = this.debitorIban;

    this.fintechSinglePaymentInitiationService
      .initiateSinglePayment(
        this.bankId,
        this.accountId,
        '',
        '',
        okurl,
        notOkUrl,
        paymentRequest,
        this.storageService.getSettings().paymentRequiresAuthentication,
        'response'
      )
      .subscribe(response => {
        if (response.status === 202) {
          this.setRedirectInfo(response);
          const confirmData = this.setConfirmDataAndGet(response, paymentRequest);
          this.router.navigate(['../confirm', JSON.stringify(confirmData)], { relativeTo: this.route });
        }
      });
  }

  onDeny(): void {
    this.router.navigate(['../../../accounts'], { relativeTo: this.route });
  }

  get creditorIban() {
    return this.paymentForm.get('creditorIban');
  }

  private getDebitorIban(accountId: string): string {
    const list = this.storageService.getLoa();
    if (list === null) {
      throw new Error('no cached list of accounts available.');
    }
    for (const a of list) {
      if (a.resourceId === accountId) {
        return a.iban;
      }
    }
    throw new Error('did not find account for id:' + accountId);
  }

  private setRedirectInfo(response: HttpResponse<any>): void {
    this.storageService.setRedirect(
      response.headers.get(HeaderConfig.HEADER_FIELD_REDIRECT_CODE),
      response.headers.get(HeaderConfig.HEADER_FIELD_AUTH_ID),
      response.headers.get(HeaderConfig.HEADER_FIELD_X_XSRF_TOKEN),
      parseInt(response.headers.get(HeaderConfig.HEADER_FIELD_REDIRECT_X_MAX_AGE), 0),
      RedirectType.PIS
    );
  }

  private setConfirmDataAndGet(
    response: HttpResponse<any>,
    paymentRequest: SinglePaymentInitiationRequest
  ): ConfirmData {
    const location = response.headers.get(HeaderConfig.HEADER_FIELD_LOCATION);
    const confirmData = new ConfirmData();
    confirmData.paymentRequest = paymentRequest;
    confirmData.redirectStruct = new RedirectStruct();
    confirmData.redirectStruct.redirectUrl = encodeURIComponent(location);
    confirmData.redirectStruct.redirectCode = response.headers.get(HeaderConfig.HEADER_FIELD_REDIRECT_CODE);
    confirmData.redirectStruct.bankId = this.bankId;
    confirmData.redirectStruct.bankName = this.storageService.getBankName();
    return confirmData;
  }
}
