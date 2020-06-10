export class RedirectStruct {
  okUrl: string;
  cancelUrl: string;
  redirectCode: string;
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
