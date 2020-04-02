import { HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import * as uuid from 'uuid';
import { DocumentCookieService } from '../services/document-cookie.service';
import { Consts } from '../models/consts';
import { LocalStorage } from '../models/local-storage';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private cookieService: DocumentCookieService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return this.handleRequest(request, next).pipe(
      catchError(errors => {
        return throwError(errors);
      })
    );
  }

  private handleRequest(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const xRequestID = uuid.v4();

    let headers;
    if (LocalStorage.isLoggedIn()) {
      headers = request.headers
        .set(Consts.HEADER_FIELD_X_REQUEST_ID, xRequestID)
        .set(Consts.HEADER_FIELD_CONTENT_TYPE, 'application/json')
        .set(Consts.HEADER_FIELD_X_XSRF_TOKEN, localStorage.getItem(Consts.LOCAL_STORAGE_XSRF_TOKEN));

      // TODO: is supposed to be sent automatically when X-XSRF cookie exists, check why not
      // Propably because it is mentioned in the api and thus overwritten by
      // generated service with not passed in XSRF-TOKEN (peters remark)
    } else {
      headers = request.headers
        .set(Consts.HEADER_FIELD_X_REQUEST_ID, xRequestID)
        .set(Consts.HEADER_FIELD_CONTENT_TYPE, 'application/json');
    }

    request = request.clone({
      withCredentials: true,
      headers
    });

    console.log('REQUEST ' + request.url + ' has ' + Consts.HEADER_FIELD_X_REQUEST_ID + ' ' + xRequestID);

    return next.handle(request);
  }
}
