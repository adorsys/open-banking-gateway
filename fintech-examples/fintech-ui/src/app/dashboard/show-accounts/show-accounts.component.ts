import { Component, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { AccountDetails } from '../../api';
import { Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { AisService } from '../services/ais.service';

@Component({
  selector: 'app-show-accounts',
  templateUrl: './show-accounts.component.html',
  styleUrls: ['./show-accounts.component.scss']
})
export class ShowAccountsComponent implements OnInit, OnDestroy {
  private accountsSubscription: Subscription;
  accounts: AccountDetails[];

  config = {
    headline: 'small',
    subheadline: 'large',
    shadow: 'shadow'
  };

  @Input()
  showAccounts = false;

  @Input()
  bankId = '';

  constructor(private route: ActivatedRoute, private aisService: AisService) {}

  ngOnInit() {
    if (this.showAccounts) {
      this.route.params.forEach(param => {
        this.aisService.getAccounts(param.id).subscribe(accounts => {
          this.accounts = accounts;
        });
      });
    }
  }

  ngOnDestroy(): void {
    this.accountsSubscription.unsubscribe();
  }
}
