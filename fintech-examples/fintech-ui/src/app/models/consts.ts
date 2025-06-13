export class Consts {
  public static LOCAL_STORAGE_XSRF_TOKEN = 'XSRF_TOKEN';
  public static LOCAL_STORAGE_SETTINGS = 'FINTECH_SETTINGS';
  public static CONFIRM_PAYMENT = 'confirmationData';
}

export class HeaderConfig {
  public static HEADER_FIELD_X_MAX_AGE = 'X-SESSION-MAX-AGE';
  public static HEADER_FIELD_REDIRECT_X_MAX_AGE = 'X-REDIRECT-MAX-AGE';
  public static HEADER_FIELD_X_XSRF_TOKEN = 'X-XSRF-TOKEN';
  public static HEADER_FIELD_X_REQUEST_ID = 'X-Request-ID';
  public static HEADER_FIELD_CONTENT_TYPE = 'Content-Type';
  public static HEADER_FIELD_LOCATION = 'location';
  public static HEADER_FIELD_AUTH_ID = 'Auth-ID';
  public static HEADER_FIELD_REDIRECT_CODE = 'Fintech-Redirect-Code';
}

// do not change to en-US, this will contain AM/PM and thus parsing will fail
export function toLocaleString(date: Date): string {
  // console.log("time is " + date.toLocaleString('en-GB'));
  return date.toLocaleString('en-GB');
}

export enum Consent {
  OK = 'OK',
  NOT_OK = 'NOT_OK'
}

export enum Payment {
  OK = 'OK',
  NOT_OK = 'NOT_OK'
}

export enum LoARetrievalInformation {
  FROM_TPP_WITH_AVAILABLE_CONSENT = 'FROM_TPP_WITH_AVAILABLE_CONSENT',
  FROM_TPP_WITH_NEW_CONSENT = 'FROM_TPP_WITH_NEW_CONSENT'
}

export enum LoTRetrievalInformation {
  FROM_TPP_WITH_AVAILABLE_CONSENT = 'FROM_TPP_WITH_AVAILABLE_CONSENT',
  FROM_TPP_WITH_NEW_CONSENT = 'FROM_TPP_WITH_NEW_CONSENT'
}

export enum ConsentSettingType {
  NONE = 'NONE',
  DEFAULT = 'DEFAULT',
  CUSTOM = 'CUSTOM'
}
