import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { BankProfile, FinTechBankSearchService } from '../../api';

@Injectable({
  providedIn: 'root'
})
export class BankProfileService {
  constructor(private finTechBankSearchService: FinTechBankSearchService) {}

  getBankProfile(bankId): Observable<BankProfile> {
    // required headers are set in http interceptor
    // that's the reason for empty strings
    return this.finTechBankSearchService.bankProfileGET('', '', bankId).pipe(
      map(response => {
        return response.bankProfile;
      })
    );
  }
}
