import { NgModule, ModuleWithProviders, SkipSelf, Optional } from '@angular/core';
import { Configuration } from './configuration';
import { HttpClient } from '@angular/common/http';


import { FinTechAccountInformationService } from './api/finTechAccountInformation.service';
import { FinTechAuthorizationService } from './api/finTechAuthorization.service';
import { FinTechBankSearchService } from './api/finTechBankSearch.service';
import { FintechRetrieveAllSinglePaymentsService } from './api/fintechRetrieveAllSinglePayments.service';
import { FintechSinglePaymentInitiationService } from './api/fintechSinglePaymentInitiation.service';

@NgModule({
  imports:      [],
  declarations: [],
  exports:      [],
  providers: [
    FinTechAccountInformationService,
    FinTechAuthorizationService,
    FinTechBankSearchService,
    FintechRetrieveAllSinglePaymentsService,
    FintechSinglePaymentInitiationService ]
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
