export * from './psuAuthentication.service';
import { PsuAuthenticationService } from './psuAuthentication.service';
export * from './psuAuthenticationAndConsentApproval.service';
import { PsuAuthenticationAndConsentApprovalService } from './psuAuthenticationAndConsentApproval.service';
export const APIS = [PsuAuthenticationService, PsuAuthenticationAndConsentApprovalService];
