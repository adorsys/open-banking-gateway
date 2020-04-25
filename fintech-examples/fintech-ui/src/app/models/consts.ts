export class Consts {
  public static LOCAL_STORAGE_XSRF_TOKEN = 'XSRF_TOKEN';

  // do not change to en-US, this will contain AM/PM and thus parsing will fail
  public static toLocaleString(date: Date): string {
    // console.log("time is " + date.toLocaleString('en-GB'));
    return date.toLocaleString('en-GB');
  };
}

export class HeaderConfig {
  public static HEADER_FIELD_X_XSRF_TOKEN = 'X-XSRF-TOKEN';
  public static HEADER_FIELD_X_REQUEST_ID = 'X-Request-ID';
  public static HEADER_FIELD_CONTENT_TYPE = 'Content-Type';
  public static HEADER_FIELD_LOCATION = 'location';
  public static HEADER_FIELD_AUTH_ID = 'Auth-ID';
  public static HEADER_FIELD_REDIRECT_CODE = 'Fintech-Redirect-Code';
}


