import { Component } from '@angular/core';
import { BankSearchService } from './services/bank-search.service';
import { ActivatedRoute, Router } from '@angular/router';
import { BankDescriptor } from '../api';
import {StorageService} from "../services/storage.service";

@Component({
  selector: 'app-bank-search',
  templateUrl: './bank-search.component.html',
  styleUrls: ['./bank-search.component.scss']
})
export class BankSearchComponent {
  searchedBanks: BankDescriptor[] = [];
  selectedBank: string;

  constructor(private bankSearchService: BankSearchService, private storageService: StorageService, private route: ActivatedRoute, private router: Router) {}

  onSearch(keyword: string): void {
    if (keyword && keyword.trim()) {
      this.bankSearchService.searchBanks(keyword).subscribe(bankDescriptor => {
        this.searchedBanks = bankDescriptor.bankDescriptor;
      });
    } else {
      this.bankUnselect();
    }
  }

  onBankSelect(bank: BankDescriptor): void {
    this.selectedBank = bank.uuid;
    this.storageService.setBankName(bank.bankName);
    this.router.navigate(['/bank', bank.uuid]);
  }

  private bankUnselect(): void {
    this.searchedBanks = [];
    this.selectedBank = null;
  }
}
