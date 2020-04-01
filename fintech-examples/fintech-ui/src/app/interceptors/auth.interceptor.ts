import { HttpEvent, HttpHandler,  HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import * as uuid from 'uuid';
import {HeaderConfig} from '../models/consts';
import {StorageService} from "../services/storage.service";
import {AuthService} from "../services/auth.service";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private sessionService: StorageService, private authService: AuthService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return this.handleRequest(request, next).pipe(
      catchError(errors => {
        return throwError(errors);
      })
    );
  }

  private handleRequest(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const xRequestID = uuid.v4();
    const xsrfToken = this.sessionService.getXsrfToken();

    let headers;
    if (this.authService.isLoggedIn()) {
      headers = request.headers
        .set(HeaderConfig.HEADER_FIELD_X_REQUEST_ID, xRequestID)
        .set(HeaderConfig.HEADER_FIELD_CONTENT_TYPE, 'application/json')
        .set(HeaderConfig.HEADER_FIELD_X_XSRF_TOKEN, xsrfToken);

      // TODO: is supposed to be sent automatically when X-XSRF cookie exists, check why not
      // Propably because it is mentioned in the api and thus overwritten by
      // generated service with not passed in XSRF-TOKEN (peters remark)
    } else {
      headers = request.headers
        .set(HeaderConfig.HEADER_FIELD_X_REQUEST_ID, xRequestID)
        .set(HeaderConfig.HEADER_FIELD_CONTENT_TYPE, 'application/json');
    }

    request = request.clone({
      withCredentials: true,
      headers
    });

    console.log('REQUEST ' + request.url + ' has ' + HeaderConfig.HEADER_FIELD_X_REQUEST_ID + ' ' + xRequestID);

    return next.handle(request);
  }
}
