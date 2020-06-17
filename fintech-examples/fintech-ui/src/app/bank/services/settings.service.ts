import { Injectable } from '@angular/core';
import { LoARetrievalInformation, LoTRetrievalInformation } from '../../models/consts';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SettingsService {

  private loa = new BehaviorSubject<LoARetrievalInformation>(LoARetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT);
  private lot = new BehaviorSubject<LoTRetrievalInformation>(LoTRetrievalInformation.FROM_TPP_WITH_AVAILABLE_CONSENT);

  constructor() { }

  public setLoA(loa: LoARetrievalInformation) {
    this.loa.next(loa);
  }

  public getLoA(): Observable<string> {
    return this.loa.asObservable();
  }

  public setLoT(lot: LoTRetrievalInformation) {
    this.lot.next(lot);
  }

  public getLoT(): Observable<string> {
    return this.lot.asObservable();
  }

}
