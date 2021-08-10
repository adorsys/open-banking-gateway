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
  FINE_GRAINED = 'FINE_GRAINED',
  CUSTOM = 'CUSTOM_CONSENT'
}
