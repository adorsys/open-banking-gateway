import { Component, OnInit } from '@angular/core';
import { AccountDetails } from '../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { AisService } from '../services/ais.service';
import { RedirectStruct } from '../redirect-page/redirect-struct';

@Component({
  selector: 'app-list-accounts',
  templateUrl: './list-accounts.component.html',
  styleUrls: ['./list-accounts.component.scss']
})
export class ListAccountsComponent implements OnInit {
  accounts: AccountDetails[];
  selectedAccount: string;
  bankId = '';

  constructor(private router: Router, private route: ActivatedRoute, private aisService: AisService) {}

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
          const location = encodeURIComponent(response.headers.get('location'));
          const r = new RedirectStruct();
          r.okUrl = location;
          r.cancelUrl = this.router.url.replace('/account', '');
          this.router.navigate(['redirect', JSON.stringify(r)], { relativeTo: this.route });
          break;
        case 200:
          this.accounts = response.body.accounts;
      }
    });
  }
}
