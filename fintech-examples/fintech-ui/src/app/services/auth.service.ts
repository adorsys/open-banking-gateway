import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Credentials } from '../models/credentials.model';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private router: Router) {}

  login(credentials: Credentials): Observable<any> {
    // TODO: must be implemented
    return of(true);
  }

  logout(): void {
    // TODO: must be implemented
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    // TODO: must be implemented
    return true;
  }
}
