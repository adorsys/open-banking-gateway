import { HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return this.handleRequest(request, next).pipe(
      catchError(errors => {
        return throwError(errors);
      })
    );
  }

  private handleRequest(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const headers = new HttpHeaders()
      // TODO: to be defined
      .set('X-Request-ID', '99391c7e-ad88-49ec-a2ad-99ddcb1f7721')
      .set('Content-Type', 'application/json')
      // TODO: is supposed to be sent automatically when X-XSRF cookie exists, check why not
      .set('X-XSRF-TOKEN', this.authService.getX_XSRF_TOKEN());

    if (this.authService.isLoggedIn()) {
      request = request.clone({
        withCredentials: true,
        headers
      });
    }
    return next.handle(request);
  }
}
