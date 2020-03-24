import {map} from 'rxjs/operators';
import {FinTechAccountInformationService} from '../../api';
import {ActivatedRoute, Router} from '@angular/router';
import {Injectable} from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class AisService {
  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private finTechAccountInformationService: FinTechAccountInformationService
  ) {
  }

  getAccounts(bankId: string) {
    const okurl = window.location.pathname;
    console.log('redirect url:' + okurl);
    return this.finTechAccountInformationService
      .aisAccountsGET(bankId, '', '', okurl, 'not-ok-url', 'response')
      .pipe(map(response => response));
  }

  getTransactions(bankId: string, accountId: string) {
    const okurl = window.location.pathname;
    return this.finTechAccountInformationService
      .aisTransactionsGET(bankId, accountId, '', '', okurl, 'not-ok-url',
        null, null, null, null, null, 'response');
  }
}
