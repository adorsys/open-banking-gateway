import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Credentials } from '../models/credentials.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor() {}

  login(credentials: Credentials): Observable<any> {
    return of(true);
  }
}
