import { Component, OnInit } from '@angular/core';
import { BankSearchService } from '../../services/bank-search.service';
import { Bank } from '../../models/bank.model';

@Component({
  selector: 'app-bank-search',
  templateUrl: './bank-search.component.html',
  styleUrls: ['./bank-search.component.scss']
})
export class BankSearchComponent {
  searchedBanks: Bank[];
  selectedBank: string;

  constructor(private bankSearchService: BankSearchService) {}

  onSearch(keyword: string) {
    this.bankSearchService.searchBanks(keyword).subscribe((banks: Bank[]) => (this.searchedBanks = banks));
  }

  onBankSelect(bankId: string) {
    this.selectedBank = bankId;
  }

  bankUnselect() {
    this.searchedBanks = [];
    this.selectedBank = null;
  }
}
