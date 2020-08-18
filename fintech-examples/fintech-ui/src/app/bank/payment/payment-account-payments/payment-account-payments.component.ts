import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FintechRetrieveAllSinglePaymentsService, PaymentInitiationWithStatusResponse } from '../../../api';

@Component({
  selector: 'app-list-payments',
  templateUrl: './payment-account-payments.component.html',
  styleUrls: ['./payment-account-payments.component.scss']
})
export class PaymentAccountPaymentsComponent implements OnInit {
  public static ROUTE = 'payments';
  list: PaymentInitiationWithStatusResponse[];

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private fintechRetrieveAllSinglePaymentsService: FintechRetrieveAllSinglePaymentsService
  ) {}

  ngOnInit() {
    const bankId = this.route.snapshot.paramMap.get('bankid');
    const accountId = this.route.snapshot.paramMap.get('accountid');
    this.fintechRetrieveAllSinglePaymentsService.retrieveAllSinglePayments(bankId, accountId, '', '', 'response')
      .pipe(map(response => response))
      .subscribe(
        response => {
          this.list = response.body;
        }
      );
  }

  initiateSinglePayment() {
    console.log('go to initiate');
    this.router.navigate(['../initiate'], { relativeTo: this.route });
  }

  onDeny() {
    this.router.navigate(['../../../accounts'], { relativeTo: this.route });
  }
}
