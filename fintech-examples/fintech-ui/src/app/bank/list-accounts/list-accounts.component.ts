import { Component, OnInit } from '@angular/core';
import { AccountDetails } from '../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { AisService } from '../services/ais.service';
import { RedirectStruct } from '../redirect-page/redirect-struct';
import {HeaderConfig} from '../../models/consts';
import {StorageService} from '../../services/storage.service';

@Component({
  selector: 'app-list-accounts',
  templateUrl: './list-accounts.component.html',
  styleUrls: ['./list-accounts.component.scss']
})
export class ListAccountsComponent implements OnInit {
  accounts: AccountDetails[];
  selectedAccount: string;
  bankId = '';

  constructor(private router: Router,
              private route: ActivatedRoute,
              private aisService: AisService,
              private storageService: StorageService) {}

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

  private loadAccount(): void {
    this.aisService.getAccounts(this.bankId).subscribe(response => {
      switch (response.status) {
        case 202:
          const location = encodeURIComponent(response.headers.get(HeaderConfig.HEADER_FIELD_LOCATION));
          this.storageService.setAuthId(response.headers.get(HeaderConfig.HEADER_FIELD_AUTH_ID));
          this.storageService.setXsrfToken(response.headers.get(HeaderConfig.HEADER_FIELD_X_XSRF_TOKEN));
          this.storageService.setRedirectActive(true);
          const r = new RedirectStruct();
          r.okUrl = location;
          r.cancelUrl = this.router.url.replace('/account', ''); // TODO this is wrong, server has to be called to get new sessionCookie
          this.router.navigate(['redirect', JSON.stringify(r)], { relativeTo: this.route });
          break;
        case 200:
          this.accounts = response.body.accounts;
      }
    });
  }
}
