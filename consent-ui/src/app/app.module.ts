import { BrowserModule } from '@angular/platform-browser';
import { ErrorHandler, NgModule } from '@angular/core';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AppRoutingModule } from './app-routing.module';
import { InfoModule } from './errorsHandler/info/info.module';
import { GlobalErrorHandler } from './errorsHandler/global-errors-handler';
import { ErrorService } from './errorsHandler/error.service';

import { AppComponent } from './app.component';
import { BASE_PATH } from './api';
import { environment } from '../environments/environment';

import { BASE_PATH as BASE_PATH_AUTH } from './api-auth';
import { SimpleTimer } from 'src/app/utilities/simple-timer';
import { CookieRenewalService } from './ais/common/cookie-renewal/CookieRenewalService';

@NgModule({
  declarations: [AppComponent],
  bootstrap: [AppComponent],
  imports: [BrowserAnimationsModule, BrowserModule, AppRoutingModule, InfoModule],
  providers: [
    SimpleTimer,
    CookieRenewalService,
    ErrorService,
    { provide: ErrorHandler, useClass: GlobalErrorHandler },
    { provide: BASE_PATH, useValue: environment.API_BASE_PATH },
    { provide: BASE_PATH_AUTH, useValue: environment.API_BASE_PATH },
    provideHttpClient(withInterceptorsFromDi())
  ]
})
export class AppModule {}
