export class RedirectStruct {
  redirectUrl: string;
  redirectCode: string;
  bankId: string;
  bankName: string;
}

export class RedirectTupelForMap {
  authId: string;
  xsrfToken: string;
  validUntil: Date;
  redirectType: RedirectType;

}

export enum RedirectType {
  AIS,
  PIS
}
