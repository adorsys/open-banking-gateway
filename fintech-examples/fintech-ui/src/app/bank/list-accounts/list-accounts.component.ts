import { Component, OnInit } from '@angular/core';
import { AccountDetails } from '../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { AisService } from '../services/ais.service';
import { AccountStruct, RedirectStruct, RedirectType } from '../redirect-page/redirect-struct';
import { HeaderConfig } from '../../models/consts';
import { StorageService } from '../../services/storage.service';

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
  id = 'dfdfdfd4drrrrr-444rr33-er43';
  id1 = '4443fdfd4drrrrr-444rr33-er43';
  iban = 'DE27500105174211347925';
  iban1 = 'DE27500105174211347926';
  name = 'bob';
  name1 = 'tom';
  name2 = 'thoma';
  iban2 = 'DE22500105174211347926';
  id2 = '4443fdfd-4ddde-444rr33-er43';

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private aisService: AisService,
    private storageService: StorageService
  ) {}

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
    return !acc.iban || acc.iban.length === 0 ? acc.bban : acc.iban;
  }

  private loadAccount(): void {
    this.aisService.getAccounts(this.bankId, this.storageService.getSettings().loa).subscribe(response => {
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
          // this is added to register url where to forward
          // if LoT is cancelled after redirect page is displayed
          // to be removed when issue https://github.com/adorsys/open-banking-gateway/issues/848 is resolved
          // or Fintech UI refactored
          this.accounts = response.body.accounts;
          const loa = [];
          loa.push(new AccountStruct(this.id, this.iban, this.name));
          loa.push(new AccountStruct(this.id1, this.iban1, this.name1));
          //          loa.push(new AccountStruct(this.id2, this.iban2, this.name2));
          this.accounts.push(loa.pop());
          this.accounts.push(loa.pop());
          //          this.accounts.push(loa.pop());
          for (const accountDetail of this.accounts) {
            loa.push(new AccountStruct(accountDetail.resourceId, accountDetail.iban, accountDetail.name));
          }
          this.storageService.setLoa(loa);
      }
    });
  }
}
