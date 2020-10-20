export class AisConsentToGrant {
  level: AccountAccessLevel;
  consent: AisConsent;

  constructor(public extras?: { [key: string]: string }) {}
}

export interface AisConsent {
  access: AccountAccess;
  recurringIndicator;
  validUntil: string;
  frequencyPerDay: number;
}

export class AccountAccess {
  accounts: string[];
  balances: string[];
  transactions: string[];

  availableAccounts: AccountAccessLevel;
  allPsd2: string;
}

export enum AccountAccessLevel {
  ALL_ACCOUNTS = 'ALL_ACCOUNTS',
  ALL_PSD2 = 'ALL_PSD2',
  ALL_ACCOUNTS_WITH_BALANCES = 'ALL_ACCOUNTS_WITH_BALANCES',
  FINE_GRAINED = 'FINE_GRAINED'
}
