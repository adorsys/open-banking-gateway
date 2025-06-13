import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { StorageService } from '../../../services/storage.service';
import { ValidatorService } from 'angular-iban';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { AccountCardComponent } from '../../common/account-card/account-card.component';
import { SharedModule } from '../../../common/shared.module';
import { RouteUtilsService } from '../../../services/route-utils.service';

@Component({
  selector: 'app-list-accounts-for-payment',
  templateUrl: './payment-accounts.component.html',
  styleUrls: ['./payment-accounts.component.scss'],
  standalone: true,
  imports: [AccountCardComponent, SharedModule]
})
export class PaymentAccountsComponent implements OnInit {
  public static ROUTE = 'accounts';
  selectedAccount;
  bankId: string;
  accounts = [];
  ibanForm: UntypedFormGroup;

  constructor(
    private storageService: StorageService,
    private formBuilder: UntypedFormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private routeUtils: RouteUtilsService
  ) {}

  ngOnInit() {
    this.bankId = this.routeUtils.getBankId(this.route);
    this.accounts = this.storageService.getLoa(this.bankId);

    this.ibanForm = this.formBuilder.group({
      iban: ['', [ValidatorService.validateIban, Validators.required]]
    });
  }

  onSelectAccount(id) {
    console.log('router navigate to ../account');
    this.selectedAccount = id;
    this.router.navigate(['../account', id], { relativeTo: this.route });
  }

  initiateSinglePayment() {
    const iban = this.ibanForm.get('iban').value.replace(/\s/g, '');
    this.router.navigate(['../account/initiate'], { relativeTo: this.route, queryParams: { iban } });
  }

  get iban() {
    return this.ibanForm.get('iban');
  }
}
