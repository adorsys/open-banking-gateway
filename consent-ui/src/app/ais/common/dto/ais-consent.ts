import {ConsentAuth} from '../../../api';
import SupportedType = ConsentAuth.SupportedConsentTypesEnum;

export class AisConsentToGrant {
  level: AccountAccessLevel;
  consent: AisConsent;

  constructor(public extras?: {[key: string]: string}) {
  }
}

export interface AisConsent {

  access: AccountAccess;
  recurringIndicator;
  validUntil: string;
  frequencyPerDay: number;
}

export class AccountAccess {

  accounts: AccountReference[];
  balances: AccountReference[];
  transactions: AccountReference[];

  availableAccounts: AccountAccessLevel;
  allPsd2: string;
}

export interface AccountReference {
  bban?: string;
  currency?: string;
  iban?: string;
  maskedPan?: string;
  msisdn?: string;
  pan?: string;
}

export enum AccountAccessLevel {

  ALL_ACCOUNTS = 'ALL_ACCOUNTS',
  ALL_PSD2 = 'ALL_PSD2',
  ALL_ACCOUNTS_WITH_BALANCES = 'ALL_ACCOUNTS_WITH_BALANCES',
  FINE_GRAINED = 'FINE_GRAINED'
}


export const AccountAccessLevelAspspConsentSupport = new Map<AccountAccessLevel, Set<SupportedType>>([
  [AccountAccessLevel.ALL_ACCOUNTS, new Set([SupportedType.GLOBALACCOUNTS, SupportedType.GLOBALALL])],
  [AccountAccessLevel.ALL_PSD2, new Set([SupportedType.GLOBALALL])],
  [AccountAccessLevel.ALL_ACCOUNTS_WITH_BALANCES, new Set([SupportedType.GLOBALALL])],
  [AccountAccessLevel.FINE_GRAINED, new Set([SupportedType.DEDICATEDALL, SupportedType.GLOBALALL, SupportedType.GLOBALACCOUNTS])]
]);
