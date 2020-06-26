import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { StorageService } from '../../../services/storage.service';
import { AccountStruct } from '../../redirect-page/redirect-struct';

@Component({
  selector: 'app-list-payments',
  templateUrl: './payment-account.component.html',
  styleUrls: ['./payment-account.component.scss']
})
export class PaymentAccountComponent implements OnInit {
  public static ROUTE = 'account';
  account:AccountStruct;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private storageService: StorageService
  ) {}


  ngOnInit() {
    // const bankId = this.route.snapshot.paramMap.get('bankid');
    const accountId = this.route.snapshot.paramMap.get('accountid');
    this.account = this.getSelectedAccount(accountId);
    this.router.navigate(['payments'], { relativeTo: this.route });
  }

  private getSelectedAccount(accountId: string) : AccountStruct {
    const list = this.storageService.getLoa();
    if (list === null) {
      throw new Error('no cached list of accounts available.');
    }
    for (const a of list) {
      if (a.resourceId === accountId) {
        return a;
      }
    }
    throw new Error('did not find account for id:' + accountId);
  }
}
