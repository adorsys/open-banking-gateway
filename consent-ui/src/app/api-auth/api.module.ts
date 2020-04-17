import { ModuleWithProviders, NgModule, Optional, SkipSelf } from '@angular/core';
import { Configuration } from './configuration';
import { HttpClient } from '@angular/common/http';


import { PsuAuthenticationService } from './api/psuAuthentication.service';
import { PsuAuthenticationAndConsentApprovalService } from './api/psuAuthenticationAndConsentApproval.service';

@NgModule({
  imports:      [],
  declarations: [],
  exports:      [],
  providers: [
    PsuAuthenticationService,
    PsuAuthenticationAndConsentApprovalService ]
})
export class ApiModule {
    public static forRoot(configurationFactory: () => Configuration): ModuleWithProviders {
        return {
            ngModule: ApiModule,
            providers: [ { provide: Configuration, useFactory: configurationFactory } ]
        };
    }

    constructor( @Optional() @SkipSelf() parentModule: ApiModule,
                 @Optional() http: HttpClient) {
        if (parentModule) {
            throw new Error('ApiModule is already loaded. Import in your base AppModule only.');
        }
        if (!http) {
            throw new Error('You need to import the HttpClientModule in your AppModule! \n' +
            'See also https://github.com/angular/angular/issues/20575');
        }
    }
}
