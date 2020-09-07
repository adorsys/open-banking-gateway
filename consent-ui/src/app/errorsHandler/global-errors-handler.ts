import { ErrorHandler, Injectable, Injector, NgZone } from '@angular/core';
import { ErrorService } from './error.service';
import { HttpErrorResponse } from '@angular/common/http';
import { InfoService } from './info/info.service';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  constructor(private zone: NgZone, private injector: Injector) {}

  handleError(error) {
    console.error(error);
    let message = 'Something went wrong';
    let severity;

    const errorService = this.injector.get(ErrorService);
    const infoService = this.injector.get(InfoService);

    if (error.status === 400) {
      error = 'User does not exist';
      severity = 'error';
    }

    if (error instanceof HttpErrorResponse) {
      message = errorService.getServerMessage(error); // Server Error
      console.log('SEVERITY ', severity)
    } else {
      message = errorService.getClientMessage(error); // Client Error
      console.log('SEVERITY ', severity)
    }

    this.zone.run(() => {
      infoService.openFeedback(message, {
        severity: severity
      });
    });
  }
}
