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

  availableAccounts: string;
  allPsd2: string;
}
