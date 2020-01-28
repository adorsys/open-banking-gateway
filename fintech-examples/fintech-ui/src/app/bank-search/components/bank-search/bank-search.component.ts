import { Component } from '@angular/core';
import { BankSearchService } from '../../services/bank-search.service';
import { Bank, BankDescriptor } from '../../models/bank.model';

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
    this.bankSearchService.searchBanks(keyword).subscribe((bankDescriptor: BankDescriptor) => {
      this.searchedBanks = bankDescriptor.bankDescriptor;
    });
  }

  onBankSelect(bankId: string) {
    this.selectedBank = bankId;
  }

  bankUnselect() {
    this.searchedBanks = [];
    this.selectedBank = null;
  }
}
