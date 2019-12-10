import { Injectable } from '@angular/core';
import {Bank} from "../models/bank.model";
import {Observable, of} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BankSearchService {

  readonly API_PATH = '/to-be-defined';

  private static MOST_POPULAR_BANKS_STUBS: Bank[] = [
    {id: '0', name: 'Sparkasse Nürnberg', bic: 'SSXXXX', bankCode: 373737},
    {id: '1', name: 'N26', bic: 'SSXXXX', bankCode: 473737},
    {id: '2', name: 'Deutsche Bank', bic: 'SSXYXX', bankCode: 373307},
    {id: '3', name: 'Sparda Nürnberg', bic: 'SSXXXX', bankCode: 488737},
    {id: '4', name: 'Commerzbank', bic: 'SSXXXX', bankCode: 488737}
  ];

  private static ALL_BANKS_STUBS: Bank[] = [
    {id: '0', name: 'Sparkasse Nürnberg', bic: 'SSXXXX', bankCode: 373737},
    {id: '1', name: 'N26', bic: 'SSXXXX', bankCode: 473737},
    {id: '2', name: 'Deutsche Bank', bic: 'SSXYXX', bankCode: 373307},
    {id: '3', name: 'Sparda Nürnberg', bic: 'SSXXXX', bankCode: 488737},
    {id: '4', name: 'Commerzbank', bic: 'SSXXXX', bankCode: 488737},
    {id: '5', name: 'Sparkasse Aachen', bic: 'SSXXXX', bankCode: 373737},
    {id: '6', name: 'Stadtsparkasse Augsburg', bic: 'SSXXXX', bankCode: 373737},
    {id: '7', name: 'Landesbank Berlin - Berliner Sparkasse', bic: 'SSXXXX', bankCode: 373737},
    {id: '8', name: 'BHW Bausparkasse', bic: 'SSXXXX', bankCode: 373737},
    {id: '9', name: 'Bausparkasse Mainz', bic: 'SSXXXX', bankCode: 373737},
    {id: '10', name: 'Sparkasse Hochfranken', bic: 'SSXXXX', bankCode: 373737},
  ];

  constructor() { }

  getPopularBanks(): Observable<Bank[]> {
    return of(BankSearchService.MOST_POPULAR_BANKS_STUBS);
  }

  searchBanks(keyword: string): Observable<Bank[]> {
    return of(BankSearchService.ALL_BANKS_STUBS.filter(bank =>
      bank.name.toLocaleLowerCase().includes(keyword.toLocaleLowerCase())
    ).slice(0, 5));
  }
}
