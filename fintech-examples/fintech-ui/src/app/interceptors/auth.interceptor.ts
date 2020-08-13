import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import * as uuid from 'uuid';
import { HeaderConfig } from '../models/consts';
import { StorageService } from '../services/storage.service';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private router: Router, private storageService: StorageService, private authService: AuthService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return this.handleRequest(request, next).pipe(
      tap(response => {
        if (response instanceof HttpResponse) {
          const maxAge = response.headers.get(HeaderConfig.HEADER_FIELD_X_MAX_AGE);
          if (maxAge !== null) {
            this.storageService.extendSessionAge(parseInt(maxAge, 0));
          }
        }
      }),
      catchError(errors => {
        return throwError(errors);
      })
    );
  }

  private handleRequest(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const xRequestID = uuid.v4();
    let headers;
    if (this.storageService.isLoggedIn()) {
      let xsrfToken = null;
      if (request.headers.get(HeaderConfig.HEADER_FIELD_X_XSRF_TOKEN) !== '') {
        xsrfToken = request.headers.get(HeaderConfig.HEADER_FIELD_X_XSRF_TOKEN);
      } else {
        xsrfToken = this.storageService.getXsrfToken();
      }
      headers = request.headers
        .set(HeaderConfig.HEADER_FIELD_X_REQUEST_ID, xRequestID)
        .set(HeaderConfig.HEADER_FIELD_CONTENT_TYPE, 'application/json')
        .set(HeaderConfig.HEADER_FIELD_X_XSRF_TOKEN, xsrfToken);
    } else {
      headers = request.headers
        .set(HeaderConfig.HEADER_FIELD_X_REQUEST_ID, xRequestID)
        .set(HeaderConfig.HEADER_FIELD_CONTENT_TYPE, 'application/json');
    }

    request = request.clone({
      withCredentials: true,
      headers
    });

    return next.handle(request);
  }
}
