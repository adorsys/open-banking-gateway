import { Component, OnInit } from '@angular/core';
import { AccountDetails } from '../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { AisService } from '../services/ais.service';
import { AccountStruct, RedirectStruct, RedirectType } from '../redirect-page/redirect-struct';
import { HeaderConfig } from '../../models/consts';
import { StorageService } from '../../services/storage.service';
import { SettingsService } from '../services/settings.service';
import { tap } from 'rxjs/operators';

@Component({
  selector: 'app-list-accounts',
  templateUrl: './list-accounts.component.html',
  styleUrls: ['./list-accounts.component.scss']
})
export class ListAccountsComponent implements OnInit {
  accounts: AccountDetails[];
  selectedAccount: string;
  bankId = '';
  loARetrievalInformation;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private aisService: AisService,
    private storageService: StorageService,
    private settingsService: SettingsService
  ) {
    this.settingsService.getLoA().pipe(tap(el => this.loARetrievalInformation = el)).subscribe();
  }

  ngOnInit() {
    this.bankId = this.route.snapshot.paramMap.get('bankid');
    this.loadAccount();
  }

  selectAccount(id) {
    this.selectedAccount = id;
  }

  isSelected(id) {
    return id === this.selectedAccount ? 'selected' : 'unselected';
  }

  visibleAccountNumber(acc: AccountDetails) {
    return (!acc.iban || acc.iban.length === 0) ? acc.bban : acc.iban
  }

  private loadAccount(): void {
    this.aisService.getAccounts(this.bankId, this.loARetrievalInformation).subscribe(response => {
      switch (response.status) {
        case 202:
          this.storageService.setRedirect(
            response.headers.get(HeaderConfig.HEADER_FIELD_REDIRECT_CODE),
            response.headers.get(HeaderConfig.HEADER_FIELD_AUTH_ID),
            response.headers.get(HeaderConfig.HEADER_FIELD_X_XSRF_TOKEN),
            parseInt(response.headers.get(HeaderConfig.HEADER_FIELD_REDIRECT_X_MAX_AGE), 0),
            RedirectType.AIS
          );
          const r = new RedirectStruct();
          r.redirectUrl = encodeURIComponent(response.headers.get(HeaderConfig.HEADER_FIELD_LOCATION));
          r.redirectCode = response.headers.get(HeaderConfig.HEADER_FIELD_REDIRECT_CODE);
          r.bankId = this.bankId;
          r.bankName = this.storageService.getBankName();
          this.router.navigate(['redirect', JSON.stringify(r)], { relativeTo: this.route });
          break;
        case 200:
          this.accounts = response.body.accounts;
          const loa = [];
          for (const accountDetail of this.accounts) {
            loa.push(new AccountStruct(accountDetail.resourceId, accountDetail.iban, accountDetail.name));
          }
          this.storageService.setLoa(loa);
      }
    });
  }
}
