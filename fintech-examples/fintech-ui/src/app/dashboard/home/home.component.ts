import { Component, OnDestroy, OnInit } from '@angular/core';
import { AisService } from '../services/ais.service';
import { Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { concatMap } from 'rxjs/operators';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit, OnDestroy {
  private accountsSubscription: Subscription;
  private bankId = '';

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

  constructor(private route: ActivatedRoute, private aisService: AisService) {}

  ngOnInit() {
    this.accountsSubscription = this.route.params
      .pipe(concatMap(param => this.aisService.getAccounts(param.id)))
      .subscribe(accountList => {
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
