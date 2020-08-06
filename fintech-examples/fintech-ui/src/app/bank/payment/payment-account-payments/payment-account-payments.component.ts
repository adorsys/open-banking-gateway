import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FintechRetrieveAllSinglePaymentsService, PaymentInitiationWithStatusResponse } from '../../../api';
import {HeaderConfig} from "../../../models/consts";
import {RedirectStruct, RedirectType} from "../../redirect-page/redirect-struct";
import {StorageService} from "../../../services/storage.service";


@Component({
  selector: 'app-list-payments',
  templateUrl: './payment-account-payments.component.html',
  styleUrls: ['./payment-account-payments.component.scss']
})
export class PaymentAccountPaymentsComponent implements OnInit {
  public static ROUTE = 'payments';
  list: PaymentInitiationWithStatusResponse[];
  bankId = '';

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private storageService: StorageService,
    private fintechRetrieveAllSinglePaymentsService: FintechRetrieveAllSinglePaymentsService
  ) {}

  ngOnInit() {
    const bankId = this.route.snapshot.paramMap.get('bankid');
    const accountId = this.route.snapshot.paramMap.get('accountid');
    let okurl = window.location.pathname;
    const notOkUrl = okurl.replace('/payment/.*', '/payment/accounts');
    okurl = okurl.replace('/initiate', '/payments');
    console.log('set urls to ', okurl, ' ', notOkUrl);

    this.fintechRetrieveAllSinglePaymentsService.retrieveAllSinglePayments(
      bankId, accountId, '', '', okurl, notOkUrl, 'response'
    ).subscribe(response => {
      switch (response.status) {
        case 202:
          this.storageService.setRedirect(
            response.headers.get(HeaderConfig.HEADER_FIELD_REDIRECT_CODE),
            response.headers.get(HeaderConfig.HEADER_FIELD_AUTH_ID),
            response.headers.get(HeaderConfig.HEADER_FIELD_X_XSRF_TOKEN),
            parseInt(response.headers.get(HeaderConfig.HEADER_FIELD_REDIRECT_X_MAX_AGE), 0),
            RedirectType.PIS
          );
          const r = new RedirectStruct();
          r.redirectUrl = encodeURIComponent(response.headers.get(HeaderConfig.HEADER_FIELD_LOCATION));
          r.redirectCode = response.headers.get(HeaderConfig.HEADER_FIELD_REDIRECT_CODE);
          r.bankId = this.bankId;
          r.bankName = this.storageService.getBankName();

          console.log('NOW GO TO:', decodeURIComponent(r.redirectUrl));
          window.location.href = decodeURIComponent(r.redirectUrl);
          break;
        case 200:
          this.list = response.body;
      }
    });
  }

  initiateSinglePayment() {
    console.log('go to initiate');
    this.router.navigate(['../initiate'], { relativeTo: this.route });
  }

  onDeny() {
    this.router.navigate(['../../../accounts'], { relativeTo: this.route });
  }
}
