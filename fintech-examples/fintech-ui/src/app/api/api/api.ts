export * from './finTechAccountInformation.service';
import { FinTechAccountInformationService } from './finTechAccountInformation.service';
import { FinTechAuthorizationService } from './finTechAuthorization.service';
import { FinTechBankSearchService } from './finTechBankSearch.service';
import { FinTechOauth2AuthenticationService } from './finTechOauth2Authentication.service';
import { FintechRetrieveAllSinglePaymentsService } from './fintechRetrieveAllSinglePayments.service';
import { FintechSinglePaymentInitiationService } from './fintechSinglePaymentInitiation.service';

export * from './finTechAuthorization.service';
export * from './finTechBankSearch.service';
export * from './finTechOauth2Authentication.service';
export * from './fintechRetrieveAllSinglePayments.service';
export * from './fintechSinglePaymentInitiation.service';
export const APIS = [FinTechAccountInformationService, FinTechAuthorizationService, FinTechBankSearchService, FinTechOauth2AuthenticationService, FintechRetrieveAllSinglePaymentsService, FintechSinglePaymentInitiationService];
