import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { FinTechAuthorizationService } from '../api';
import { Credentials } from '../models/credentials.model';
import { Consts } from '../common/consts';
import * as uuid from 'uuid';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private router: Router, private finTechAuthorizationService: FinTechAuthorizationService) {}

  login(credentials: Credentials): Observable<boolean> {
    return this.finTechAuthorizationService.loginPOST(uuid.v4(), credentials, 'response').pipe(
      map(response => {
        console.log('cookies:' + document.cookie);
        return response.ok;
      })
    );
  }

  logout(): void {
    localStorage.clear();
    document.cookie = Consts.XSRF_TOKEN + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    document.cookie = Consts.SESSION_COOKIE + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    this.openLoginPage();
  }

  openLoginPage() {
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return document.cookie.split(';').some(item => item.trim().startsWith(Consts.XSRF_TOKEN + '='));
  }

  getX_XSRF_TOKEN(): string {
    for (const cookie of document.cookie.split(';')) {
      if (cookie.trim().startsWith(Consts.XSRF_TOKEN + '=')) {
        return cookie.trim().substr(Consts.XSRF_TOKEN.length + 1);
      }
    }
    // throw "did not find Cookie for " + Consts.XSRF_TOKEN;
    console.error('did not find Cookie for ' + Consts.XSRF_TOKEN);
  }
}
