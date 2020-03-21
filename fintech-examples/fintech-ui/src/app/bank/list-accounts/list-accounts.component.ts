import {Component, OnDestroy, OnInit} from '@angular/core';
import {AccountDetails} from '../../api';
import {Subscription} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {AisService} from '../services/ais.service';

@Component({
  selector: 'app-list-accounts',
  templateUrl: './list-accounts.component.html'
})
export class ListAccountsComponent implements OnInit, OnDestroy {
  private accountsSubscription: Subscription;
  accounts: AccountDetails[];
  selectedAccount: string;
  bankId = '';

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private aisService: AisService) {
  }

  ngOnInit() {
    this.route.parent.parent.paramMap.subscribe(p => {
        this.bankId = p.get('bankid');
        console.log("BANKID IS ", this.bankId);
      }
    );

    console.log('list-accounts for bankid', this.bankId);
    this.accountsSubscription = this.aisService.getAccounts(this.bankId).subscribe(
      response => {
        switch (response.status) {
          case 202:
            const additionalParameters = new URLSearchParams({
              authorizationSessionId: response.headers.get('Authorization-Session-ID'),
              serviceSessionId: response.headers.get('Service-Session-ID'),
              redirectCode: response.headers.get('Redirect-Code')
            });
            const location = encodeURIComponent(response.headers.get('location'));
            this.router.navigate(['redirect', location], {relativeTo: this.route});
            break;
          case 200:
            console.log('I got ', response.body.accounts.length, ' accounts');
            this.accounts = response.body.accounts;
        }
      });

  }


  ngOnDestroy(): void {
    if (this.accountsSubscription != null) {
      this.accountsSubscription.unsubscribe();
    }
  }

  selectAccount(id) {
    this.selectedAccount = id;
  }

  isSelected(id) {
    return id === this.selectedAccount ? 'selected' : 'unselected';
  }
}
