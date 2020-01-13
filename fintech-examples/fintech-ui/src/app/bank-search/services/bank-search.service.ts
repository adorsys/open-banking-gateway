import { Injectable } from '@angular/core';
import { Bank } from '../models/bank.model';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class BankSearchService {
  // path resolved by proxy
  readonly API_PATH = 'fintech-api-proxy';

  constructor(private http: HttpClient) {}

  searchBanks(keyword: string): Observable<Bank[]> {
    return this.http.get<Bank[]>(this.API_PATH + '/v1/banks/fts', {
      params: {
        q: keyword,
        max_results: '5'
      }
    });
  }
}
