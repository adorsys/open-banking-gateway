import { ErrorHandler, Injectable, Injector, NgZone } from '@angular/core';
import { ErrorService } from './error.service';
import { HttpErrorResponse } from '@angular/common/http';
import { InfoService } from './info/info.service';
import { Router } from '@angular/router';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  constructor(private zone: NgZone, private injector: Injector) {}

  handleError(error) {
    const errorService = this.injector.get(ErrorService);
    const infoService = this.injector.get(InfoService);

    let message = null;

    if (error instanceof HttpErrorResponse) {
      if (error.status === 401) {
        console.log('status was 401');
        this.router.navigate(['/session-expired']);
      } else if (error.status === 403) {
        console.log('status was 403');
        this.router.navigate(['/forbidden-oauth2']);
      } else {
        message = errorService.getServerMessage(error);
      }
    } else {
      // Client Error
      message = errorService.getClientMessage(error);
    }

    this.zone.run(() => {
      if (message !== null) {
        infoService.openFeedback(message, {
          severity: 'error'
        });
      }
    });
  }

  get router(): Router {
    return this.injector.get(Router);
  }
}
