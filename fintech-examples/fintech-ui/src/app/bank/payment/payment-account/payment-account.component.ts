import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { StorageService } from '../../../services/storage.service';
import { AccountStruct } from '../../redirect-page/redirect-struct';
import { AccountDetails } from '../../../api';
import { Consts } from '../../../models/consts';

@Component({
  selector: 'app-list-payments',
  templateUrl: './payment-account.component.html',
  styleUrls: ['./payment-account.component.scss']
})
export class PaymentAccountComponent implements OnInit {
  public static ROUTE = 'account';
  account: AccountDetails;
  bankId: string;

  constructor(private router: Router, private route: ActivatedRoute, private storageService: StorageService) {}

  ngOnInit() {
    this.bankId = this.route.snapshot.params[Consts.BANK_ID_NAME];
    const accountId = this.route.snapshot.params[Consts.ACCOUNT_ID_NAME];

    this.account = {...this.getSelectedAccount(accountId), currency: ''};
    this.router.navigate(['payments'], {relativeTo: this.route});
  }

  private getSelectedAccount(accountId: string): AccountStruct {
    const list = this.storageService.getLoa(this.bankId);
    if (list === null) {
      throw new Error('no cached list of accounts available.');
    }
    for (const a of list) {
      if (a.resourceId === accountId) {
        return a;
      } else if (a.iban === accountId) {
        return a;
      }
    }
    throw new Error('did not find account for id:' + accountId);
  }
}
