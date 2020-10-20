export * from './authStateConsentAuthorization.service';
import {AuthStateConsentAuthorizationService} from './authStateConsentAuthorization.service';

export * from './fromASPSPConsentAuthorization.service';
import {FromASPSPConsentAuthorizationService} from './fromASPSPConsentAuthorization.service';

export * from './updateConsentAuthorization.service';
import {UpdateConsentAuthorizationService} from './updateConsentAuthorization.service';

export const APIS = [AuthStateConsentAuthorizationService, FromASPSPConsentAuthorizationService, UpdateConsentAuthorizationService];
