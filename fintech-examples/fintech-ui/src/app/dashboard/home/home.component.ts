import { Component, OnInit, OnDestroy } from '@angular/core';
import { AisService } from '../services/ais.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit, OnDestroy {
  private accountsSubscription: Subscription;

  cardList = [
    {
      headline: 'Telecom',
      subheadline: 'Monatliche Abrechnung Mobilfunkvertrag',
      accountBalance: '-2128'
    },
    {
      headline: 'Telecom',
      subheadline: 'Monatliche Abrechnung Mobilfunkvertrag',
      accountBalance: '128'
    },
    {
      headline: 'Telecom',
      subheadline: 'Monatliche Abrechnung Mobilfunkvertrag',
      accountBalance: '-128'
    },
    {
      headline: 'Telecom',
      subheadline: 'Monatliche Abrechnung Mobilfunkvertrag',
      accountBalance: '128'
    }
  ];

  cardList2 = [];

  config = {
    headline: 'small',
    subheadline: 'large',
    shadow: 'shadow'
  };

  constructor(private aisService: AisService) {}

  ngOnInit() {
    this.accountsSubscription = this.aisService.getAccounts().subscribe(accountList => {
      accountList.accounts.forEach(account => {
        this.cardList2.push({
          headline: account.iban,
          subheadline: account.name ? account.name : ''
        });
      });
    });
  }

  ngOnDestroy(): void {
    this.accountsSubscription.unsubscribe();
  }
}
