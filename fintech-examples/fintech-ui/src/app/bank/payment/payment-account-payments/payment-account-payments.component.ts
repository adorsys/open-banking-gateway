import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { map } from 'rxjs/operators';

import { FintechRetrieveAllSinglePaymentsService, PaymentInitiationWithStatusResponse } from '../../../api';
import { PaymentCardComponent } from '../../common/payment-card/payment-card.component';
import { RouteUtilsService } from '../../../services/route-utils.service';

@Component({
  selector: 'app-list-payments',
  templateUrl: './payment-account-payments.component.html',
  styleUrls: ['./payment-account-payments.component.scss'],
  standalone: true,
  imports: [PaymentCardComponent]
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
    private fintechRetrieveAllSinglePaymentsService: FintechRetrieveAllSinglePaymentsService,
    private routeUtils: RouteUtilsService
  ) {}

  ngOnInit() {
    this.bankId = this.routeUtils.getBankId(this.route);
    this.accountId = this.routeUtils.getAccountId(this.route);

    this.fintechRetrieveAllSinglePaymentsService
      .retrieveAllSinglePayments(this.bankId, this.accountId, '', '', 'response')
      .pipe(map((response) => response))
      .subscribe((response) => {
        this.list = response.body;
      });
  }

  initiateSinglePayment() {
    console.log('go to initiate');
    this.router.navigate(['../initiate'], { relativeTo: this.route });
  }

  onDeny() {
    this.location.back();
  }
}
