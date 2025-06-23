import { Component, OnInit } from '@angular/core';
import { AccountDetails } from '../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { AisService } from '../services/ais.service';
import { AccountStruct, RedirectType } from '../redirect-page/redirect-struct';
import { HeaderConfig } from '../../models/consts';
import { StorageService } from '../../services/storage.service';
import { SharedModule } from '../../common/shared.module';
import { RouteUtilsService } from '../../services/route-utils.service';

@Component({
  selector: 'app-list-accounts',
  templateUrl: './list-accounts.component.html',
  styleUrls: ['./list-accounts.component.scss'],
  standalone: true,
  imports: [SharedModule]
})
export class ListAccountsComponent implements OnInit {
  accounts: AccountDetails[];
  bankId = '';

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private aisService: AisService,
    private storageService: StorageService,
    private routeUtils: RouteUtilsService
  ) {}

  ngOnInit() {
    this.bankId = this.routeUtils.getBankId(this.route);
    this.loadAccount();
  }

  onSubmit(id: string) {
    this.router
      .navigate([id], { relativeTo: this.route })
      .then((success) => {
        if (!success) {
          console.warn(`Navigation to account ${id} was not successful`);
        }
        return success;
      })
      .catch((error) => {
        console.error(`Navigation error: ${error}`);
        return false;
      });
  }

  private loadAccount(): void {
    const settings = this.storageService.getSettings();
    const online = !this.storageService.isAfterRedirect() && !settings.cacheLoa;
    this.storageService.setAfterRedirect(false);

    this.aisService
      .getAccounts(
        this.bankId,
        settings.loa,
        JSON.stringify(settings.consent),
        settings.withBalance,
        online,
        settings.consentRequiresAuthentication
      )
      .subscribe((response) => {
        switch (response.status) {
          case 202: {
            this.storageService.setRedirect(
              response.headers.get(HeaderConfig.HEADER_FIELD_REDIRECT_CODE),
              response.headers.get(HeaderConfig.HEADER_FIELD_AUTH_ID),
              response.headers.get(HeaderConfig.HEADER_FIELD_X_XSRF_TOKEN),
              parseInt(response.headers.get(HeaderConfig.HEADER_FIELD_REDIRECT_X_MAX_AGE), 0),
              RedirectType.AIS
            );

            const r = this.storageService.createRedirectStruct(
              response.headers.get(HeaderConfig.HEADER_FIELD_LOCATION),
              response.headers.get(HeaderConfig.HEADER_FIELD_REDIRECT_CODE),
              this.bankId
            );

            this.router.navigate(['redirect', JSON.stringify(r)], { relativeTo: this.route });
            break;
          }
          case 200: {
            // this is added to register url where to forward
            // if LoT is cancelled after redirect page is displayed
            // to be removed when issue https://github.com/adorsys/open-banking-gateway/issues/848 is resolved
            // or Fintech UI refactored
            this.accounts = response.body.accounts;
            const loa = [];
            for (const accountDetail of this.accounts) {
              loa.push(new AccountStruct(accountDetail.resourceId, accountDetail.iban, accountDetail.name));
            }
            this.storageService.setLoa(this.bankId, loa);
          }
        }
      });
  }
}
