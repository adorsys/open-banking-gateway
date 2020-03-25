import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {Observable} from 'rxjs';
import {map, subscribeOn} from 'rxjs/operators';
import {FinTechAuthorizationService} from '../api';
import {Credentials} from '../models/credentials.model';
import {Consts} from '../common/consts';
import {DocumentCookieService} from './document-cookie.service';
import {LocalStorage} from "../common/local-storage";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(
    private router: Router,
    private finTechAuthorizationService: FinTechAuthorizationService,
    private cookieService: DocumentCookieService
  ) {
  }

  login(credentials: Credentials): Observable<boolean> {
    this.logout();
    return this.finTechAuthorizationService.loginPOST('', credentials, 'response').pipe(
      map(response => {
        this.cookieService.getAll().forEach(cookie => console.log('cookie after login :' + cookie));
        LocalStorage.login(response.headers.get(Consts.HEADER_FIELD_X_XSRF_TOKEN));
        if (!LocalStorage.isLoggedIn()) {
          console.log("login not sucessfull");
          this.openLoginPage();
        }
        localStorage.setItem(Consts.LOCAL_STORAGE_USERNAME, credentials.username);
        return response.ok;
      })
    );
  }

  logout(): Observable<boolean> {
    console.log('start logout');
    return this.finTechAuthorizationService.logoutPOST('', '', 'response').pipe(
      map(
        response => {
          console.log("got response from server");
          localStorage.clear();
          this.cookieService.delete(Consts.COOKIE_NAME_SESSION);
          LocalStorage.logout();
          this.cookieService.getAll().forEach(cookie => console.log('cookie after logout :' + cookie));
          this.openLoginPage();
          return response.ok;
        },
        error => {
          console.error('logout with error');
        }
    ));
  }

  openLoginPage() {
    this.router.navigate(['/login']);
  }
}
