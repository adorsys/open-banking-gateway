import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { BankProfile } from '../models/bank-profile.model';

@Injectable({
  providedIn: 'root'
})
export class BankProfileService {
  // path resolved by proxy
  readonly API_PATH = `${environment.FINTECH_API}`;

  constructor(private http: HttpClient) {}

  getBankProfile(bankId): Observable<BankProfile> {
    return this.http.get<BankProfile>(this.API_PATH + '/banks/profile', {
      params: {
        id: bankId
      }
    });
  }
}
