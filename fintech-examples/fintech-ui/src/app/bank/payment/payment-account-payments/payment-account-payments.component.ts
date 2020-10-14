import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FintechRetrieveAllSinglePaymentsService, PaymentInitiationWithStatusResponse} from '../../../api';
import {map} from 'rxjs/operators';
import {Consts} from '../../../models/consts';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {StorageService} from '../../../services/storage.service';
import {Location} from '@angular/common';
import {ValidatorService} from "angular-iban";

@Component({
  selector: 'app-list-payments',
  templateUrl: './payment-account-payments.component.html',
  styleUrls: ['./payment-account-payments.component.scss']
})
export class PaymentAccountPaymentsComponent implements OnInit {
  public static ROUTE = 'payments';
  list: PaymentInitiationWithStatusResponse[];
  ibanForm: FormGroup;
  isRandomAccountId: boolean;
  bankId: string;
  accountId: string;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private location: Location,
    private storageService: StorageService,
    private fintechRetrieveAllSinglePaymentsService: FintechRetrieveAllSinglePaymentsService
  ) {
  }

  ngOnInit() {
    this.bankId = this.route.snapshot.params[Consts.BANK_ID_NAME];
    this.accountId = this.route.snapshot.params[Consts.ACCOUNT_ID_NAME];

    this.isRandomAccountId = this.accountId === Consts.RANDOM_ACCOUNT_ID;
    this.ibanForm = this.formBuilder.group({
      iban: ['', [ValidatorService.validateIban, Validators.required]]
    });

    if (!this.isRandomAccountId) {
      this.fintechRetrieveAllSinglePaymentsService
        .retrieveAllSinglePayments(this.bankId, this.accountId, '', '', 'response')
        .pipe(map((response) => response))
        .subscribe((response) => {
          this.list = response.body;
        });
    }
  }

  initiateSinglePayment() {
    console.log('go to initiate');
    if (this.isRandomAccountId) {
      this.storageService.setLoa(this.bankId, [{
        resourceId: this.accountId,
        iban: this.ibanForm.get('iban').value.replace(/\s/g, ""), //remove white space
        name: ''
      }])
    }
    this.router.navigate(['../initiate'], {relativeTo: this.route});
  }

  onDeny() {
    this.location.back();
  }

  get iban() {
    return this.ibanForm.get('iban');
  }
}
