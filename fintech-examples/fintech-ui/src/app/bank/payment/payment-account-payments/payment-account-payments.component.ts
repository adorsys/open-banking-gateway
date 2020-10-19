import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Location} from '@angular/common';
import {map} from 'rxjs/operators';

import {FintechRetrieveAllSinglePaymentsService, PaymentInitiationWithStatusResponse} from '../../../api';
import {Consts} from '../../../models/consts';
import {StorageService} from '../../../services/storage.service';

@Component({
  selector: 'app-list-payments',
  templateUrl: './payment-account-payments.component.html',
  styleUrls: ['./payment-account-payments.component.scss']
})
export class PaymentAccountPaymentsComponent implements OnInit {
  public static ROUTE = 'payments';
  list: PaymentInitiationWithStatusResponse[];
  bankId: string;
  accountId: string;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private location: Location,
    private storageService: StorageService,
    private fintechRetrieveAllSinglePaymentsService: FintechRetrieveAllSinglePaymentsService
  ) {
  }

  ngOnInit() {
    this.bankId = this.route.snapshot.params[Consts.BANK_ID_NAME];
    this.accountId = this.route.snapshot.params[Consts.ACCOUNT_ID_NAME];

    this.fintechRetrieveAllSinglePaymentsService
      .retrieveAllSinglePayments(this.bankId, this.accountId, '', '', 'response')
      .pipe(map((response) => response))
      .subscribe((response) => {
        this.list = response.body;
      });
  }

  initiateSinglePayment() {
    console.log('go to initiate');
    this.router.navigate(['../initiate'], {relativeTo: this.route});
  }

  onDeny() {
    this.location.back();
  }
}
