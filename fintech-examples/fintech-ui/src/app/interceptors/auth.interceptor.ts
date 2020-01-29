import { HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable, throwError as observableThrowError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { fromPromise } from 'rxjs/internal-compatibility';
import { Injectable } from '@angular/core';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return fromPromise(this.handleRequest(request, next)).pipe(
      catchError(errors => {
        return observableThrowError(errors);
      })
    );
  }

  private async handleRequest(request: HttpRequest<any>, next: HttpHandler): Promise<HttpEvent<any>> {
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
    return next.handle(request).toPromise();
  }
}
