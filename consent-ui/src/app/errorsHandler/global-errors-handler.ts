import {ErrorHandler, Injectable, Injector, NgZone} from '@angular/core';
import {ErrorService} from './error.service';
import {HttpErrorResponse} from '@angular/common/http';
import {InfoService} from './info/info.service';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  constructor(private zone: NgZone, private injector: Injector) {
  }

  handleError(error) {
    console.error(error);

    const errorService = this.injector.get(ErrorService);
    const infoService = this.injector.get(InfoService);

    let message = 'Something went wrong';               // default ErrorMessage

    if (error instanceof HttpErrorResponse) {
      message = errorService.getServerMessage(error); // Server Error
    } else {
      message = errorService.getClientMessage(error); // Client Error
    }

    this.zone.run(() => {
      infoService.openFeedback(message, {
        severity: 'error'
      });
    });
  }
}
