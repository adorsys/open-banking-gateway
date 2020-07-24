import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FintechRetrieveAllSinglePaymentsService, PaymentInitiationWithStatusResponse} from '../../../api';
import {map, tap} from 'rxjs/operators';
import {SettingsService} from "../../services/settings.service";

@Component({
  selector: 'app-list-payments',
  templateUrl: './payment-account-payments.component.html',
  styleUrls: ['./payment-account-payments.component.scss']
})
export class PaymentAccountPaymentsComponent implements OnInit {
  public static ROUTE = 'payments';
  list : PaymentInitiationWithStatusResponse[];
  paymentRequiresAuthentication = false

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private settingsService: SettingsService,
    private fintechRetrieveAllSinglePaymentsService: FintechRetrieveAllSinglePaymentsService
  ) {
    this.settingsService.getPaymentRequiresAuthentication().pipe(tap(el => this.paymentRequiresAuthentication = el)).subscribe();
  }

  ngOnInit() {
    const bankId = this.route.snapshot.paramMap.get('bankid');
    const accountId = this.route.snapshot.paramMap.get('accountid');
    this.fintechRetrieveAllSinglePaymentsService.retrieveAllSinglePayments(bankId, accountId, '', '', this.paymentRequiresAuthentication, 'response')
      .pipe(map(response => response))
      .subscribe(
        response => {
          this.list = response.body;
        }
      );
  }

  initiateSinglePayment( ) {
    console.log('go to initiate');
    this.router.navigate(['../initiate'], { relativeTo: this.route });
  }

  onDeny() {
    this.router.navigate(['../../../accounts'], { relativeTo: this.route });
  }
}
