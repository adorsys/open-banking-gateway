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
            console.log('max age', maxAge, ' set for call ', request.url);
          } else {
            console.log('NO max age for call ', request.url);
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
    let xsrfToken = null;
    let headers;
    console.log('request is going to ', request.url);
    if (request.headers.get(HeaderConfig.HEADER_FIELD_X_XSRF_TOKEN) !== '') {
      xsrfToken = request.headers.get(HeaderConfig.HEADER_FIELD_X_XSRF_TOKEN);
      console.log('xsrf token is set, so we do not change it', xsrfToken);
    } else {
      xsrfToken = this.storageService.getXsrfToken();
      console.log('xsrf token taken from  storage ', xsrfToken);
    }
    if (this.storageService.isLoggedIn()) {
      headers = request.headers
        .set(HeaderConfig.HEADER_FIELD_X_REQUEST_ID, xRequestID)
        .set(HeaderConfig.HEADER_FIELD_CONTENT_TYPE, 'application/json')
        .set(HeaderConfig.HEADER_FIELD_X_XSRF_TOKEN, xsrfToken);
      console.log('OUTGOING REQUEST ' + request.url + ' has xsrftoken ' + xsrfToken);
    } else {
      headers = request.headers
        .set(HeaderConfig.HEADER_FIELD_X_REQUEST_ID, xRequestID)
        .set(HeaderConfig.HEADER_FIELD_CONTENT_TYPE, 'application/json');
      console.log('OUTGOING REQUEST ' + request.url + ' without xsrftoken');
    }

    request = request.clone({
      withCredentials: true,
      headers
    });

    return next.handle(request);
  }
}
