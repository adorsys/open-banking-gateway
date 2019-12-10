import {Injectable} from '@angular/core';
import {BankSearchService} from "./services/bank-search.service";
import {BankSearchState} from "./state/bank-search.state";
import {Observable} from "rxjs";
import {Bank} from "./models/bank.model";
import {tap} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class BankSearchFacade {

  constructor(private bankSearchService: BankSearchService,
              private bankSearchState: BankSearchState) {
  }

  isUpdating$(): Observable<boolean> {
    return this.bankSearchState.isUpdating$();
  }

  getPopularBanks(): Observable<Bank[]> {
    this.bankSearchState.setUpdating(true);
    return this.bankSearchService.getPopularBanks()
      .pipe(
        tap(banks => {
          this.bankSearchState.setUpdating(false);
          return banks
        })
      );
  }

  searchBanks(keyword: string): Observable<Bank[]> {
    this.bankSearchState.setUpdating(true);
    return this.bankSearchService.searchBanks(keyword)
      .pipe(
        tap(banks => {
          this.bankSearchState.setUpdating(false);
          return banks
        })
      );
  }
}
