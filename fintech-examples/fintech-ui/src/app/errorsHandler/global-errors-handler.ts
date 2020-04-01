import { ErrorHandler, Injectable, Injector, NgZone } from '@angular/core';
import { ErrorService } from './error.service';
import { HttpErrorResponse } from '@angular/common/http';
import { InfoService } from './info/info.service';
import { AuthService } from '../services/auth.service';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  constructor(private zone: NgZone, private injector: Injector) {}

  handleError(error) {
    const errorService = this.injector.get(ErrorService);
    const infoService = this.injector.get(InfoService);
    const authService = this.injector.get(AuthService);

    let message = 'Something went wrong';

    if (error instanceof HttpErrorResponse) {
      // Server Error
      if (error.status === 401) {
        authService.logout();
        message = 'Please enter a valid username or password';
      } else {
        message = errorService.getServerMessage(error);
      }
    } else {
      // Client Error
      message = errorService.getClientMessage(error);
    }

    this.zone.run(() => {
      infoService.openFeedback(message, {
        severity: 'error',
        duration: 60000
      });
    });
  }
}
