import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
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
    if (this.authService.isLoggedIn()) {
      request = request.clone({
        setHeaders: {
          'X-Request-ID': '99391c7e-ad88-49ec-a2ad-99ddcb1f7721',
          'Content-Type': 'application/json',
          'x-xsrf-token': this.authService.getX_XSRF_TOKEN()
        }
      });
    }
    return next.handle(request).toPromise();
  }
}
