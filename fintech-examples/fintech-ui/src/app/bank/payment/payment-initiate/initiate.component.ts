import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {HttpResponse} from '@angular/common/http';
import {Location} from "@angular/common";
import {ActivatedRoute, Router} from '@angular/router';
import {ValidatorService} from 'angular-iban';
import {FintechSinglePaymentInitiationService, SinglePaymentInitiationRequest} from '../../../api';
import {Consts, HeaderConfig} from '../../../models/consts';
import {RedirectStruct, RedirectType} from '../../redirect-page/redirect-struct';
import {StorageService} from '../../../services/storage.service';
import {ConfirmData} from '../payment-confirm/confirm.data';

class TestPayment {
  constructor(public referenceName: string, public purpose: string) {
  }
}


@Component({
  selector: 'app-initiate',
  templateUrl: './initiate.component.html',
  styleUrls: ['./initiate.component.scss']
})
export class InitiateComponent implements OnInit {
  public static ROUTE = 'initiate';

  static TEST_PAYMENTS: TestPayment[] = [
    new TestPayment("test user", "test transfer"),
    new TestPayment("Anton", "Transfer to Anton (demo)"),
    new TestPayment("Amazon payment", "Payment for order #12345 (demo)"),
    new TestPayment("Apple", "Apple ITunes payment (demo)"),
    new TestPayment("Netflix TV", "Netflix payment (demo)")
  ];

  bankId = '';
  accountId = '';
  debtorIban: string;
  paymentForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private fintechSinglePaymentInitiationService: FintechSinglePaymentInitiationService,
    private router: Router,
    private route: ActivatedRoute,
    private location: Location,
    private storageService: StorageService
  ) {
    this.bankId = this.route.snapshot.params[Consts.BANK_ID_NAME];
    this.accountId = this.route.snapshot.params[Consts.ACCOUNT_ID_NAME];
    this.debtorIban = this.route.snapshot.queryParams.iban;
  }

  ngOnInit() {
    this.debtorIban = this.debtorIban ? this.debtorIban : this.getDebitorIban(this.accountId);
    const testPayment = InitiateComponent.TEST_PAYMENTS[Math.floor(Math.random() * InitiateComponent.TEST_PAYMENTS.length)]
    this.paymentForm = this.formBuilder.group({
      name: [testPayment.referenceName, Validators.required],
      creditorIban: ['AL90208110080000001039531801', [ValidatorService.validateIban, Validators.required]],
      amount: ['12.34', [Validators.pattern('^[1-9]\\d*(\\.\\d{1,2})?$'), Validators.required]],
      purpose: [testPayment.purpose],
      instantPayment: false
    });
  }

  onConfirm(): void {
    let okurl = this.router.url;
    console.log('okurl: ', okurl);
    let notOkUrl = okurl.replace('/payment/.*', '/payment/accounts');
    okurl = okurl.replace('/initiate', '/payments');
    console.log('set urls to ', okurl, '', notOkUrl);

    let accountId = this.accountId;
    if (!this.accountId) {
      accountId = this.debtorIban;
      const index = this.router.url.indexOf('account');
      okurl = index > 0 ? okurl.substring(0, index) + 'account' : okurl;
      okurl = accountId ? `${okurl}/${accountId}/payments` : okurl;
      notOkUrl = okurl;
    }

    const paymentRequest: SinglePaymentInitiationRequest = { ...this.paymentForm.getRawValue() };
    paymentRequest.debitorIban = this.debtorIban;
    paymentRequest.purpose = this.paymentForm.getRawValue().purpose;
    paymentRequest.instantPayment = this.paymentForm.getRawValue().instantPayment;
    this.fintechSinglePaymentInitiationService
      .initiateSinglePayment(
        this.bankId,
        accountId,
        '',
        '',
        okurl,
        notOkUrl,
        paymentRequest,
        this.storageService.getSettings().paymentRequiresAuthentication,
        'response'
      )
      .subscribe((response) => {
        if (response.status === 202) {
          this.setRedirectInfo(response);
          const confirmData = this.setConfirmDataAndGet(response, paymentRequest);
          this.router.navigate(['../confirm', JSON.stringify(confirmData)], { relativeTo: this.route });
        }
      });
  }

  onDeny(): void {
    this.location.back();
  }

  get creditorIban() {
    return this.paymentForm.get('creditorIban');
  }

  private getDebitorIban(accountId: string): string {
    const list = this.storageService.getLoa(this.bankId);
    if (list === null) {
      throw new Error(' no cached list of accounts available.');
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
