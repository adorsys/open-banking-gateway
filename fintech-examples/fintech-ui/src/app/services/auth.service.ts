import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Credentials } from '../models/credentials.model';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { CookieService } from 'ngx-cookie-service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  public URL = `${environment.FINTECH_API}`;
  private XSRF_TOKEN = 'XSRF-TOKEN';

  constructor(private router: Router, private cookieService: CookieService, private http: HttpClient) {}

  login(credentials: Credentials): Observable<any> {
    return this.http
      .post<any>(this.URL + '/login', credentials, {
        headers: new HttpHeaders({
          'X-Request-ID': '99391c7e-ad88-49ec-a2ad-99ddcb1f7721'
        }),
        observe: 'response'
      })
      .pipe(
        map(loginResponse => {
          // if login response is ok and cookie exist then the login was successful
          console.log(loginResponse);
          return loginResponse.ok && this.cookieService.check(this.XSRF_TOKEN);
        })
      );
  }

  logout(): void {
    this.cookieService.deleteAll();
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return this.cookieService.check(this.XSRF_TOKEN);
  }

  getX_XSRF_TOKEN(): string {
    return this.cookieService.get(this.XSRF_TOKEN);
  }
}
