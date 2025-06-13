import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterOutlet } from '@angular/router';
import { StorageService } from '../../../services/storage.service';
import { AccountStruct } from '../../redirect-page/redirect-struct';
import { AccountDetails } from '../../../api';
import { AccountCardComponent } from '../../common/account-card/account-card.component';
import { NgIf } from '@angular/common';
import { RouteUtilsService } from '../../../services/route-utils.service';

@Component({
  selector: 'app-list-payments',
  templateUrl: './payment-account.component.html',
  styleUrls: ['./payment-account.component.scss'],
  standalone: true,
  imports: [AccountCardComponent, RouterOutlet, NgIf]
})
export class PaymentAccountComponent implements OnInit {
  public static ROUTE = 'account';
  account: AccountDetails;
  bankId: string;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private storageService: StorageService,
    private routeUtils: RouteUtilsService
  ) {}

  ngOnInit() {
    this.bankId = this.routeUtils.getBankId(this.route);
    const accountId = this.routeUtils.getAccountId(this.route);

    this.account = { ...this.getSelectedAccount(accountId), currency: '' };
    this.router.navigate(['payments'], { relativeTo: this.route });
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
