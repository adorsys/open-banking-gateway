export class Bank {
  uuid: string;
  bankName: string;
  bic: string;
  bankCode: number;
}

export class BankDescriptor {
  bankDescriptor: Bank[];
}
