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
  bankId;
  accountId;
  account:AccountStruct;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private storageService: StorageService
  ) {}


  ngOnInit() {
    this.bankId = this.route.snapshot.paramMap.get('bankid');
    this.accountId = this.route.snapshot.paramMap.get('accountid');
    this.account = this.getSelectedAccount(this.accountId);
    console.log('lpc bankid:', this.bankId, ' accountId:', this.accountId);
    this.router.navigate(['payments'], { relativeTo: this.route });
  }

  private getSelectedAccount(accountId: string) : AccountStruct {
    const alist = this.storageService.getLoa();
    for (const a of alist) {
      if (a.resourceId === accountId) {
        return a;
      }
    }
  }
}
