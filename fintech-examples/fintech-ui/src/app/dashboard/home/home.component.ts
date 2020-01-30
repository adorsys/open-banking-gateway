import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
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

  cardList2 = [
    {
      headline: 'All Accounts',
      subheadline: '1.485,90'
    },
    {
      headline: 'Girokonto',
      subheadline: '1.277,90'
    },
    {
      headline: 'Haushaltskonto',
      subheadline: '48,00'
    },
    {
      headline: 'DE94500105174894965666',
      subheadline: '-129,90'
    }
  ];

  config = {
    headline: 'small',
    subheadline: 'large',
    shadow: 'shadow'
  };

  constructor() {}

  ngOnInit() {}
}
