export class AisConsent {

  access: AccountAccess;
  recurringIndicator = false;
  validUntil: string;
  frequencyPerDay: number;

  constructor(public extras?: {[key: string]: string}) {
  }
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
  ALL_ACCOUNTS_WITH_BALANCES = 'ALL_ACCOUNTS_WITH_BALANCES',
  FINE_GRAINED = 'FINE_GRAINED'
}
