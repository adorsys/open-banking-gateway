import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideRouter } from '@angular/router';
import { routes } from './app/app.routes';

import { enableProdMode, ErrorHandler, importProvidersFrom } from '@angular/core';
import { environment } from './environments/environment';
import {
  HTTP_INTERCEPTORS,
  provideHttpClient,
  withInterceptorsFromDi,
  withXsrfConfiguration
} from '@angular/common/http';
import { SimpleTimer } from './app/services/simple-timer';
import { AuthGuard } from './app/guards/auth.guard';
import { ErrorService } from './app/errorsHandler/error.service';
import { DocumentCookieService } from './app/services/document-cookie.service';
import { TimerService } from './app/services/timer.service';
import { AuthInterceptor } from './app/interceptors/auth.interceptor';
import { GlobalErrorHandler } from './app/errorsHandler/global-errors-handler';
import { AppModule } from './app/app.module';
import { InfoService } from './app/errorsHandler/info/info.service';
import { provideAnimations } from '@angular/platform-browser/animations';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

if (environment.production) {
  enableProdMode();
}

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    provideHttpClient(),
    provideHttpClient(
      withInterceptorsFromDi(),
      withXsrfConfiguration({
        cookieName: 'XSRF-TOKEN',
        headerName: 'X-XSRF-TOKEN'
      })
    ),
    provideAnimations(),
    importProvidersFrom(FormsModule),
    importProvidersFrom(CommonModule),
    SimpleTimer,
    AuthGuard,
    ErrorService,
    DocumentCookieService,
    TimerService,
    InfoService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    { provide: ErrorHandler, useClass: GlobalErrorHandler },
    // other providers
    importProvidersFrom(AppModule)
  ]
}).catch((err) => console.error(err));
