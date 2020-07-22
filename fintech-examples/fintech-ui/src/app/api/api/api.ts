export * from './finTechAccountInformation.service';
import { FinTechAccountInformationService } from './finTechAccountInformation.service';
import { FinTechAuthorizationService } from './finTechAuthorization.service';
import { FinTechBankSearchService } from './finTechBankSearch.service';
import { FinTechGmailAuthenticationService } from './finTechGmailAuthentication.service';
import { FintechRetrieveAllSinglePaymentsService } from './fintechRetrieveAllSinglePayments.service';
import { FintechSinglePaymentInitiationService } from './fintechSinglePaymentInitiation.service';

export * from './finTechAuthorization.service';
export * from './finTechBankSearch.service';
export * from './finTechGmailAuthentication.service';
export * from './fintechRetrieveAllSinglePayments.service';
export * from './fintechSinglePaymentInitiation.service';
export const APIS = [FinTechAccountInformationService, FinTechAuthorizationService, FinTechBankSearchService, FinTechGmailAuthenticationService, FintechRetrieveAllSinglePaymentsService, FintechSinglePaymentInitiationService];
