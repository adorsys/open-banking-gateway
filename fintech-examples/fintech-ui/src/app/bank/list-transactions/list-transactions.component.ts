import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AisService } from '../services/ais.service';
import { AccountReport } from '../../api';
import { RedirectType } from '../redirect-page/redirect-struct';
import { HeaderConfig } from '../../models/consts';
import { StorageService } from '../../services/storage.service';
import { RoutingPath } from '../../models/routing-path.model';
import { SharedModule } from '../../common/shared.module';
import { RouteUtilsService } from '../../services/route-utils.service';

@Component({
  selector: 'app-list-transactions',
  templateUrl: './list-transactions.component.html',
  styleUrls: ['./list-transactions.component.scss'],
  standalone: true,
  imports: [SharedModule]
})
export class ListTransactionsComponent implements OnInit {
  accountId = '';
  bankId = '';
  makeVisible = false;
  account;
  transactions: AccountReport;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private aisService: AisService,
    private storageService: StorageService,
    private routeUtils: RouteUtilsService
  ) {}

  ngOnInit() {
    this.bankId = this.routeUtils.getBankId(this.route);
    this.accountId = this.routeUtils.getAccountId(this.route);
    this.loadTransactions();
    this.account = this.getAccountById(this.accountId);
  }

  private loadTransactions(): void {
    const settings = this.storageService.getSettings();
    const online = !this.storageService.isAfterRedirect() && !settings.cacheLot;

    this.aisService
      .getTransactions(
        this.bankId,
        this.accountId,
        settings.lot,
        JSON.stringify(settings.consent),
        online,
        settings.consentRequiresAuthentication,
        settings.dateFrom,
        settings.dateTo
      )
      .subscribe((response) => {
        switch (response.status) {
          case 202: {
            console.log('list tx got REDIRECT');
            this.storageService.setRedirect(
              response.headers.get(HeaderConfig.HEADER_FIELD_REDIRECT_CODE),
              response.headers.get(HeaderConfig.HEADER_FIELD_AUTH_ID),
              response.headers.get(HeaderConfig.HEADER_FIELD_X_XSRF_TOKEN),
              parseInt(response.headers.get(HeaderConfig.HEADER_FIELD_X_MAX_AGE), 0),
              RedirectType.AIS
            );
            const r = this.storageService.createRedirectStruct(
              response.headers.get(HeaderConfig.HEADER_FIELD_LOCATION),
              response.headers.get(HeaderConfig.HEADER_FIELD_REDIRECT_CODE),
              this.bankId
            );
            this.router.navigate(['../redirect', JSON.stringify(r)], { relativeTo: this.route });
            break;
          }
          case 200:
            console.log('I got transactions');
            this.transactions = response.body.transactions;
            console.log(this.transactions);
            this.makeVisible = true;
        }
      });
  }

  navigateToPayment() {
    this.router.navigate([RoutingPath.PAYMENT], { relativeTo: this.route.parent.parent });
  }

  private getAccountById(id: string) {
    for (const acc of this.storageService.getLoa(this.bankId)) {
      if (acc.resourceId === id) {
        return acc;
      }
    }
    return null;
  }
}
