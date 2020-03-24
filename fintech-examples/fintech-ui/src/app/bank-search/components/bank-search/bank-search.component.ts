import { Component } from '@angular/core';
import { BankSearchService } from '../../services/bank-search.service';
import { ActivatedRoute, Router } from '@angular/router';
import { BankDescriptor } from '../../../api';
import {Consts} from "../../../common/consts";

@Component({
  selector: 'app-bank-search',
  templateUrl: './bank-search.component.html',
  styleUrls: ['./bank-search.component.scss']
})
export class BankSearchComponent {
  searchedBanks: BankDescriptor[] = [];
  selectedBank: string;

  constructor(private bankSearchService: BankSearchService, private route: ActivatedRoute, private router: Router) {}

  onSearch(keyword: string) {
    if (keyword && keyword.trim()) {
      this.bankSearchService.searchBanks(keyword).subscribe(bankDescriptor => {
        this.searchedBanks = bankDescriptor.bankDescriptor;
      });
    } else {
      this.bankUnselect();
    }
  }

  onBankSelect(bank: BankDescriptor) {
    this.selectedBank = bank.uuid;
    localStorage.setItem(Consts.LOCAL_STORAGE_BANKNAME, bank.bankName);
    this.router.navigate(['/bank', bank.uuid]);
  }

  private bankUnselect() {
    this.searchedBanks = [];
    this.selectedBank = null;
  }
}
