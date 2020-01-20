import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BankProfile } from '../models/bank-profile.model';

@Injectable({
  providedIn: 'root'
})
export class BankProfileService {
  // path resolved by proxy
  readonly API_PATH = 'fintech-api-proxy';

  constructor(private http: HttpClient) {}

  getBankProfile(bankId): Observable<BankProfile> {
    return this.http.get<BankProfile>(this.API_PATH + '/v1/banks/profile', {
      params: {
        id: bankId
      }
    });
  }
}
