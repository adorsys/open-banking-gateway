import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EMPTY, Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return this.handleRequest(req, next).pipe(
      catchError(httpErrorResponse => {
        if (httpErrorResponse.status === 401) {
          this.authService.openLoginPage();
          return EMPTY;
        } else if (httpErrorResponse.status === 403) {
          // this.authService.openAccessForbiddenDialog();
          return EMPTY;
        } else {
          return throwError(httpErrorResponse.error);
        }
      })
    );
  }

  private handleRequest(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (this.authService.isLoggedIn()) {
      request = request.clone({
        withCredentials: true,
        setHeaders: {
          'X-Request-ID': '99391c7e-ad88-49ec-a2ad-99ddcb1f7721'
        }
      });
    }
    return next.handle(request);
  }
}
