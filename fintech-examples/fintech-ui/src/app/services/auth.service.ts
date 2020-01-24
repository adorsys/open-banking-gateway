import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Credentials } from '../models/credentials.model';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  public URL = `${environment.FINTECH_API}`;
  private X_XSRF_TOKEN = 'x-xsrf-token';

  constructor(private router: Router, private http: HttpClient) {}

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
          const token = loginResponse.headers.get(this.X_XSRF_TOKEN);
          if (token && token.length > 0) {
            this.setCookie(token);
            return true;
          }
          return false;
        })
      );
  }

  logout(): void {
    localStorage.removeItem(this.X_XSRF_TOKEN);
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    if (this.getX_XSRF_TOKEN()) {
      return this.getX_XSRF_TOKEN().length > 0;
    }

    return false;
  }

  setCookie(token: string) {
    localStorage.setItem(this.X_XSRF_TOKEN, token);
  }

  getX_XSRF_TOKEN(): string {
    return localStorage.getItem(this.X_XSRF_TOKEN);
  }
}
