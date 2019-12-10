import {Component, OnInit} from '@angular/core';
import {Bank} from "../../models/bank.model";
import {BankSearchFacade} from "../../bank-search.facade";

@Component({
  selector: 'app-bank-search',
  templateUrl: './bank-search.component.html',
  styleUrls: ['./bank-search.component.scss']
})
export class BankSearchComponent implements OnInit {

  loading: boolean;
  private searchedBanks: Bank[];

  constructor(private bankSearchFacade: BankSearchFacade) {
    bankSearchFacade.isUpdating$().subscribe(loading => this.loading = loading);
  }

  ngOnInit() {
    this.bankSearchFacade
      .getPopularBanks()
      .subscribe((banks: Bank[]) => this.searchedBanks = banks);
  }

  onSearch(keyword: string) {
    this.bankSearchFacade
      .searchBanks(keyword)
      .subscribe((banks: Bank[]) => this.searchedBanks = banks);
  }

}
