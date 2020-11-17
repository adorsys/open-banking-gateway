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
      console.log('status was ', error.status);
      switch (error.status) {
        case 401:
          error = 'Incorrect Username or Password';
          message = errorService.getClientMessage(error);
          break;
        case 403:
          this.router.navigate(['/forbidden-oauth2']);
          break;
        case 410:
        case 429:
          message = this.handleConsentNotFound(error, errorService);
          break;
        default:
          message = errorService.getServerMessage(error);
          break;
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

  handleConsentNotFound(error, errorService: ErrorService): string {
    let errorCode = 'unknown';
    if (error.headers.get('X-ERROR-CODE') != null) {
      errorCode = error.headers.get('X-ERROR-CODE').trim();
    }
    console.log('got error code "' + errorCode + '"');
    switch (errorCode) {
      case 'CONSENT_UNKNOWN':
        return 'The consent is unknown. Please request for new consent by changing settings.';
      case 'CONSENT_EXPIRED':
        return 'The consent is expired. Please request for new consent by changing settings.';
      case 'CONSENT_ACCESS_EXCEEDED_LIMIT':
        return 'The consent has been used too many time. Please request for new consent by changing settings or wait till tomorrow and try again.';
        break;
      default:
        return errorService.getServerMessage(error);
        break;
    }
  }
}
