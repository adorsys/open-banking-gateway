import { Injectable } from '@angular/core';
import { BankDescriptor } from '../models/bank.model';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BankSearchService {
  // path resolved by proxy
  readonly URL = `${environment.FINTECH_API}`;

  constructor(private http: HttpClient) {}

  searchBanks(keyword: string): Observable<BankDescriptor> {
    return this.http.get<BankDescriptor>(this.URL + '/search/bankSearch', {
      params: {
        keyword,
        max_results: '5'
      }
    });
  }
}
