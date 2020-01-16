import { Component, OnInit } from '@angular/core';
import { BankSearchService } from '../../services/bank-search.service';
import { Bank } from '../../models/bank.model';

@Component({
  selector: 'app-bank-search',
  templateUrl: './bank-search.component.html',
  styleUrls: ['./bank-search.component.scss']
})
export class BankSearchComponent implements OnInit {
  public searchedBanks: Bank[];

  constructor(private bankSearchService: BankSearchService) {}

  ngOnInit() {
    this.bankSearchService.getBanks().subscribe((banks: Bank[]) => (this.searchedBanks = banks));
  }

  onSearch(keyword: string) {
    this.bankSearchService.searchBanks(keyword).subscribe((banks: Bank[]) => (this.searchedBanks = banks));
  }
}
