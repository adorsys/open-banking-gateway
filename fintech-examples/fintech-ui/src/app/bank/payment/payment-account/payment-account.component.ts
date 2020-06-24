import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FintechSinglePaymentInitiationService } from '../../../api';

@Component({
  selector: 'app-list-payments',
  templateUrl: './payment-account.component.html',
  styleUrls: ['./payment-account.component.scss']
})
export class PaymentAccountComponent implements OnInit {
  public static ROUTE = 'account';
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
    this.router.navigate(['payments'], { relativeTo: this.route });
  }

}
