export * from './psuAuthentication.service';
import { PsuAuthenticationService } from './psuAuthentication.service';
import { PsuAuthenticationAndConsentApprovalService } from './psuAuthenticationAndConsentApproval.service';

export * from './psuAuthenticationAndConsentApproval.service';
export const APIS = [PsuAuthenticationService, PsuAuthenticationAndConsentApprovalService];
