import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BankProfile } from '../models/bank-profile.model';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BankProfileService {
  // path resolved by proxy
  public URL = `${environment.FINTECH_API}`;

  constructor(private http: HttpClient) {}

  getBankProfile(bankId): Observable<BankProfile> {
    return this.http
      .get<BankProfile>(this.URL + '/search/bankProfile', {
        params: { bankId },
        observe: 'body'
      })
      .pipe(
        map((response: any) => {
          // return only bank profile
          return response.bankProfile;
        })
      );
  }
}
