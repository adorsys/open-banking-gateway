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

  onBankSelect(bankId: string) {
    this.selectedBank = bankId;
    localStorage.setItem(Consts.LOCAL_STORAGE_BANKNAME, this.searchedBanks.find(el => el.uuid === bankId).bankName);
    this.router.navigate(['/bank', bankId]);
  }

  private bankUnselect() {
    this.searchedBanks = [];
    this.selectedBank = null;
  }
}
