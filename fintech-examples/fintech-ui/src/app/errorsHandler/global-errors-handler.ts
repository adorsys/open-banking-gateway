import { ErrorHandler, Injectable, Injector, NgZone } from '@angular/core';
import { ErrorService } from './error.service';
import { HttpErrorResponse } from '@angular/common/http';
import { InfoService } from './info/info.service';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  constructor(private zone: NgZone, private injector: Injector) {}

  handleError(error) {
    const errorService = this.injector.get(ErrorService);
    const infoService = this.injector.get(InfoService);

    let message = null;

    if (error instanceof HttpErrorResponse) {
      if (error.status === 401) {

        // authService.logout();
        // does not make sense, as logout only works if user is logged in,
        // getting 401 means session cookie not valid, so logout will fails anyway
        // message = 'Please enter a valid username or password';

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
          severity: 'error',
          duration: 60000
        });
      }
    });
  }
}
