import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FintechSinglePaymentInitiationService } from '../../../api';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-list-payments',
  templateUrl: './payment-account-payments.component.html',
  styleUrls: ['./payment-account-payments.component.scss']
})
export class PaymentAccountPaymentsComponent implements OnInit {
  public static ROUTE = 'payments';
  bankId;
  accountId;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private fintechSinglePaymentInitiationService: FintechSinglePaymentInitiationService
  ) {}


  ngOnInit() {
    this.bankId = this.route.snapshot.paramMap.get('bankid');
    this.accountId = this.route.snapshot.paramMap.get('accountid');
    console.log('lpc bankid:', this.bankId, ' accountId:', this.accountId);

    this.fintechSinglePaymentInitiationService.retrieveAllSinglePayments(this.bankId, this.accountId, '', '', 'response')
      .pipe(map(response => response))
      .subscribe(
        response => {
          console.log('response status of payment call is ', response.status);
          console.log('body is :',JSON.stringify(response.body));
        }
      );
  }


  initiateSinglePayment( ) {
    console.log('go to initiate');
    this.router.navigate(['../initiate'], { relativeTo: this.route });
  }



}
