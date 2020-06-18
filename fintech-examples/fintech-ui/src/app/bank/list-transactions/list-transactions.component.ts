import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AisService } from '../services/ais.service';
import { AccountReport } from '../../api';
import { RedirectStruct, RedirectType } from '../redirect-page/redirect-struct';
import { HeaderConfig, LoTRetrievalInformation } from '../../models/consts';
import { StorageService } from '../../services/storage.service';
import { tap } from 'rxjs/operators';
import { SettingsService } from '../services/settings.service';

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
  loTRetrievalInformation;
  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private aisService: AisService,
    private storageService: StorageService,
    private settingsService: SettingsService
  ) {
    this.settingsService.getLoT().pipe(tap(el => this.loTRetrievalInformation = el)).subscribe();
  }

  ngOnInit() {
    this.bankId = this.route.parent.snapshot.paramMap.get('bankid');
    this.accountId = this.route.snapshot.paramMap.get('accountid');
    this.loadTransactions();
  }

  private loadTransactions(): void {
    this.aisService.getTransactions(this.bankId, this.accountId, this.loTRetrievalInformation).subscribe(response => {
      switch (response.status) {
        case 202:
          console.log('list tx got REDIRECT');
          this.storageService.setRedirect(
            response.headers.get(HeaderConfig.HEADER_FIELD_REDIRECT_CODE),
            response.headers.get(HeaderConfig.HEADER_FIELD_AUTH_ID),
            response.headers.get(HeaderConfig.HEADER_FIELD_X_XSRF_TOKEN),
            parseInt(response.headers.get(HeaderConfig.HEADER_FIELD_X_MAX_AGE), 0),
            RedirectType.AIS
          );
          const r = new RedirectStruct();
          r.redirectUrl = encodeURIComponent(response.headers.get(HeaderConfig.HEADER_FIELD_LOCATION));
          r.redirectCode = response.headers.get(HeaderConfig.HEADER_FIELD_REDIRECT_CODE);
          r.bankId = this.bankId;
          r.bankName = this.storageService.getBankName();
          this.router.navigate(['../redirect', JSON.stringify(r)], { relativeTo: this.route });
          break;
        case 200:
          console.log('I got transactions');
          this.transactions = response.body.transactions;
          this.makeVisible = true;
      }
    });
  }
}
