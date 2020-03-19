import { HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import * as uuid from 'uuid';
import { DocumentCookieService } from '../services/document-cookie.service';
import { Consts } from '../common/consts';

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
    const headers = request.headers
      .set(Consts.HEADER_FIELD_X_REQUEST_ID, xRequestID)
      .set(Consts.HEADER_FIELD_CONTENT_TYPE, 'application/json');

    if (this.cookieService.exists(Consts.COOKIE_NAME_XSRF_TOKEN)) {
      // TODO: is supposed to be sent automatically when X-XSRF cookie exists, check why not
      // Propably because it is mentioned in the api and thus overwritten by
      // generated service with not passed in XSRF-TOKEN (peters remark)
      headers.set(Consts.HEADER_FIELD_X_XSRF_TOKEN, this.cookieService.find(Consts.COOKIE_NAME_XSRF_TOKEN));
    }

    console.log('REQUEST ' + request.url + ' has ' + Consts.HEADER_FIELD_X_REQUEST_ID + ' ' + xRequestID);

    request = request.clone({
      withCredentials: true,
      headers
    });
    return next.handle(request);
  }
}
