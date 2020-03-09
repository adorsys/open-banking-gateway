import { Component, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { AccountDetails } from '../../api';
import { Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { AisService } from '../services/ais.service';

@Component({
  selector: 'app-list-accounts',
  templateUrl: './list-accounts.component.html'
})
export class ListAccountsComponent implements OnInit, OnDestroy {
  private accountsSubscription: Subscription;
  accounts: AccountDetails[];
  selectedAccount: string;

  @Input()
  makeVisible = false;

  @Input()
  bankId = '';

  constructor(private route: ActivatedRoute, private aisService: AisService) {}

  ngOnInit() {
    if (this.makeVisible) {
      this.route.params.forEach(param => {
        this.accountsSubscription = this.aisService.getAccounts(param.id).subscribe(accounts => {
          this.accounts = accounts;
        });
      });
    }
  }

  ngOnDestroy(): void {
    this.accountsSubscription.unsubscribe();
  }

  selectAccount(id) {
    this.selectedAccount = id;
  }

  isSelected(id) {
    return id === this.selectedAccount ? 'selected' : 'unselected';
  }
}
