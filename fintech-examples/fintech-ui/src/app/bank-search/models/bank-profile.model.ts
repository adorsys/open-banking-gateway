import { Bank } from './bank.model';

export class BankProfile {
  bankId: string;
  bankName: Bank;
  bic: string;
  services: string[];
}
