import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AisService } from '../services/ais.service';
import { AccountReport } from '../../api';
import { RedirectStruct, RedirectType } from '../redirect-page/redirect-struct';
import { HeaderConfig } from '../../models/consts';
import { StorageService } from '../../services/storage.service';

@Component({
  selector: 'app-list-transactions',
  templateUrl: './list-transactions.component.html',
  styleUrls: ['./list-transactions.component.scss']
})
export class ListTransactionsComponent implements OnInit {
  accountId = '';
  bankId = '';
  makeVisible = false;
  transactions: AccountReport;
  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private aisService: AisService,
    private storageService: StorageService
  ) {}

  ngOnInit() {
    this.bankId = this.route.parent.snapshot.paramMap.get('bankid');
    this.accountId = this.route.snapshot.paramMap.get('accountid');
    this.loadTransactions();
  }

  private loadTransactions(): void {
    this.aisService.getTransactions(this.bankId, this.accountId).subscribe(response => {
      switch (response.status) {
        case 202:
          console.log('list tx got REDIRECT');
          const location = encodeURIComponent(response.headers.get(HeaderConfig.HEADER_FIELD_LOCATION));
          this.storageService.setRedirect(
            response.headers.get(HeaderConfig.HEADER_FIELD_REDIRECT_CODE),
            response.headers.get(HeaderConfig.HEADER_FIELD_AUTH_ID),
            response.headers.get(HeaderConfig.HEADER_FIELD_X_XSRF_TOKEN),
            parseInt(response.headers.get(HeaderConfig.HEADER_FIELD_X_MAX_AGE), 0),
            RedirectType.AIS
          );
          const r = new RedirectStruct();
          const currentUrl = this.router.url;
          r.okUrl = location;
          r.cancelUrl = currentUrl.substring(0, currentUrl.indexOf('/account'));
          r.redirectCode = response.headers.get(HeaderConfig.HEADER_FIELD_REDIRECT_CODE);
          this.router.navigate(['redirect', JSON.stringify(r)], { relativeTo: this.route });
          break;
        case 200:
          console.log('I got transactions');
          this.transactions = response.body.transactions;
          this.makeVisible = true;
      }
    });
  }
}
