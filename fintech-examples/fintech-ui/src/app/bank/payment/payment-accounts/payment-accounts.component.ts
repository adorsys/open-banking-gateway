import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { StorageService } from '../../../services/storage.service';
import { Consts } from '../../../models/consts';
import { ValidatorService } from 'angular-iban';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-list-accounts-for-payment',
  templateUrl: './payment-accounts.component.html',
  styleUrls: ['./payment-accounts.component.scss']
})
export class PaymentAccountsComponent implements OnInit {
  public static ROUTE = 'accounts';
  selectedAccount;
  bankId: string;
  accounts = [];
  ibanForm: FormGroup;

  constructor(
    private storageService: StorageService,
    private formBuilder: FormBuilder,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.bankId = this.route.snapshot.params[Consts.BANK_ID_NAME];
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
